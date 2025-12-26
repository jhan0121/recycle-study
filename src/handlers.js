/**
 * 이벤트 핸들러
 */

import { STORAGE_KEYS } from './config.js';
import { ERROR_CODES } from './constants.js';
import { registerDevice, getDevices, deleteDevice, saveReviewUrl } from './api.js';
import { setStorageData, clearStorage, validateStorageForAuth } from './storage.js';
import {
  elements,
  showLoading,
  hideLoading,
  showMessage,
  showView,
  handleApiError
} from './ui.js';
import { formatDate, isValidEmail } from './utils.js';

/**
 * 디바이스 등록 버튼 클릭 핸들러
 */
export async function handleRegister() {
  const email = elements.emailInput.value.trim();

  if (!email) {
    showMessage('이메일을 입력해주세요.', 'error');
    return;
  }

  if (!isValidEmail(email)) {
    showMessage('유효한 이메일 형식이 아닙니다.', 'error');
    return;
  }

  try {
    showLoading();
    const result = await registerDevice(email);

    await setStorageData({
      [STORAGE_KEYS.EMAIL]: result.email,
      [STORAGE_KEYS.IDENTIFIER]: result.identifier,
      [STORAGE_KEYS.IS_AUTHENTICATED]: false
    });

    elements.emailDisplay.textContent = result.email;
    showView('pending');
    showMessage('이메일로 인증 링크가 전송되었습니다.', 'success');
  } catch (error) {
    showMessage(error.message, 'error');
  } finally {
    hideLoading();
  }
}

/**
 * 인증 확인 버튼 클릭 핸들러
 */
export async function handleCheckAuth() {
  try {
    showLoading();
    const storageData = await validateStorageForAuth();
    const result = await getDevices(storageData.email, storageData.identifier);

    await setStorageData({
      [STORAGE_KEYS.IS_AUTHENTICATED]: true
    });

    elements.userEmail.textContent = result.email;
    showView('main');
    showMessage('인증이 완료되었습니다!', 'success');
  } catch (error) {
    if (error.code === ERROR_CODES.UNAUTHORIZED) {
      showMessage('아직 인증이 완료되지 않았습니다.', 'info');
    } else {
      await handleApiError(error);
    }
  } finally {
    hideLoading();
  }
}

/**
 * 다른 이메일로 등록 버튼 클릭 핸들러
 */
export async function handleReset() {
  await clearStorage();
  elements.emailInput.value = '';
  showView('login');
}

/**
 * URL 저장 버튼 클릭 핸들러
 */
export async function handleSaveUrl() {
  try {
    showLoading();

    const [tab] = await chrome.tabs.query({ active: true, currentWindow: true });

    if (!tab?.url) {
      showMessage('현재 페이지의 URL을 가져올 수 없습니다.', 'error');
      return;
    }

    const storageData = await validateStorageForAuth();
    const result = await saveReviewUrl(storageData.identifier, tab.url);

    elements.scheduleDates.innerHTML = '';
    result.scheduledAts.forEach(date => {
      const li = document.createElement('li');
      li.textContent = formatDate(date);
      elements.scheduleDates.appendChild(li);
    });

    elements.saveResult.classList.remove('hidden');
    showMessage('저장되었습니다!', 'success');
  } catch (error) {
    await handleApiError(error);
  } finally {
    hideLoading();
  }
}

/**
 * 디바이스 관리 버튼 클릭 핸들러
 */
export async function handleShowDevices() {
  const isVisible = !elements.devicesSection.classList.contains('hidden');

  if (isVisible) {
    elements.devicesSection.classList.add('hidden');
    return;
  }

  try {
    showLoading();
    const storageData = await validateStorageForAuth();
    const result = await getDevices(storageData.email, storageData.identifier);

    elements.devicesList.innerHTML = '';

    result.devices.forEach(device => {
      const li = document.createElement('li');
      const isCurrentDevice = device.identifier === storageData.identifier;

      li.innerHTML = `
        <div class="device-info">
          <div class="device-id">${device.identifier.substring(0, 20)}...</div>
          <div class="device-date">${formatDate(device.createdAt)}</div>
          ${isCurrentDevice ? '<div class="current-device">현재 디바이스</div>' : ''}
        </div>
        ${!isCurrentDevice ? `<button class="btn btn-danger" data-id="${device.identifier}">삭제</button>` : ''}
      `;

      elements.devicesList.appendChild(li);
    });

    elements.devicesSection.classList.remove('hidden');
  } catch (error) {
    await handleApiError(error);
  } finally {
    hideLoading();
  }
}

/**
 * 디바이스 삭제 클릭 핸들러
 * @param {string} targetIdentifier - 삭제할 디바이스 식별자
 */
export async function handleDeleteDevice(targetIdentifier) {
  if (!confirm('이 디바이스를 삭제하시겠습니까?')) {
    return;
  }

  try {
    showLoading();
    const storageData = await validateStorageForAuth();

    await deleteDevice(
      storageData.email,
      storageData.identifier,
      targetIdentifier
    );

    showMessage('디바이스가 삭제되었습니다.', 'success');
    elements.devicesSection.classList.add('hidden');
    await handleShowDevices();
  } catch (error) {
    await handleApiError(error);
  } finally {
    hideLoading();
  }
}

/**
 * 로그아웃 버튼 클릭 핸들러
 */
export async function handleLogout() {
  if (!confirm('로그아웃 하시겠습니까?')) {
    return;
  }

  await clearStorage();
  elements.saveResult.classList.add('hidden');
  elements.devicesSection.classList.add('hidden');
  showView('login');
  showMessage('로그아웃 되었습니다.', 'info');
}
