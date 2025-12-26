/**
 * Recycle Study Chrome Extension
 * 팝업 진입점
 */

import { getStorageData, clearStorage, isStorageDataValid } from './storage.js';
import {
  elements,
  initializeElements,
  showView,
  showMessage
} from './ui.js';
import {
  handleRegister,
  handleCheckAuth,
  handleReset,
  handleSaveUrl,
  handleShowDevices,
  handleDeleteDevice,
  handleLogout
} from './handlers.js';

/**
 * 이벤트 리스너 등록
 */
function setupEventListeners() {
  elements.registerBtn.addEventListener('click', handleRegister);
  elements.checkAuthBtn.addEventListener('click', handleCheckAuth);
  elements.resetBtn.addEventListener('click', handleReset);
  elements.saveUrlBtn.addEventListener('click', handleSaveUrl);
  elements.showDevicesBtn.addEventListener('click', handleShowDevices);
  elements.logoutBtn.addEventListener('click', handleLogout);

  // 디바이스 삭제 버튼 (이벤트 위임)
  elements.devicesList.addEventListener('click', (e) => {
    if (e.target.classList.contains('btn-danger')) {
      const targetId = e.target.dataset.id;
      handleDeleteDevice(targetId);
    }
  });

  // 엔터키로 등록
  elements.emailInput.addEventListener('keypress', (e) => {
    if (e.key === 'Enter') {
      handleRegister();
    }
  });
}

/**
 * 앱 초기화
 */
async function initialize() {
  try {
    const storageData = await getStorageData();

    // 스토리지 데이터 유효성 검증
    if (!isStorageDataValid(storageData)) {
      console.warn('손상된 스토리지 데이터 감지, 초기화 진행');
      await clearStorage();
      showView('login');
      showMessage('저장된 정보에 문제가 있어 초기화되었습니다.', 'info');
      return;
    }

    if (storageData.isAuthenticated) {
      elements.userEmail.textContent = storageData.email;
      showView('main');
    } else if (storageData.email && storageData.identifier) {
      elements.emailDisplay.textContent = storageData.email;
      showView('pending');
    } else {
      showView('login');
    }
  } catch (error) {
    console.error('초기화 오류:', error);
    await clearStorage();
    showView('login');
  }
}

/**
 * DOM 로드 후 실행
 */
document.addEventListener('DOMContentLoaded', () => {
  initializeElements();
  setupEventListeners();
  initialize();
});
