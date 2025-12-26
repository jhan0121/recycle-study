/**
 * Recycle Study Chrome Extension
 * 팝업 UI 로직
 *
 * CONFIG는 config.js에서 로드됨
 */

// ============================================
// 에러 코드 정의
// ============================================
const ERROR_CODES = {
  // 로그아웃이 필요한 에러
  UNAUTHORIZED: 'UNAUTHORIZED',           // 401: 인증되지 않은 디바이스
  NOT_FOUND: 'NOT_FOUND',                 // 404: 존재하지 않는 리소스
  INVALID_STORAGE: 'INVALID_STORAGE',     // 스토리지 데이터 손상

  // 로그아웃 불필요한 에러
  BAD_REQUEST: 'BAD_REQUEST',             // 400: 잘못된 요청
  SERVER_ERROR: 'SERVER_ERROR',           // 5xx: 서버 오류
  NETWORK_ERROR: 'NETWORK_ERROR'          // 네트워크 연결 실패
};

// 자동 로그아웃이 필요한 에러 코드
const LOGOUT_REQUIRED_ERRORS = [
  ERROR_CODES.UNAUTHORIZED,
  ERROR_CODES.NOT_FOUND,
  ERROR_CODES.INVALID_STORAGE
];

// ============================================
// DOM 요소
// ============================================
const elements = {
  // 뷰
  loginView: document.getElementById('login-view'),
  pendingView: document.getElementById('pending-view'),
  mainView: document.getElementById('main-view'),

  // 로그인 화면
  emailInput: document.getElementById('email-input'),
  registerBtn: document.getElementById('register-btn'),

  // 인증 대기 화면
  emailDisplay: document.querySelector('.email-display'),
  checkAuthBtn: document.getElementById('check-auth-btn'),
  resetBtn: document.getElementById('reset-btn'),

  // 메인 화면
  userEmail: document.getElementById('user-email'),
  saveUrlBtn: document.getElementById('save-url-btn'),
  saveResult: document.getElementById('save-result'),
  scheduleDates: document.getElementById('schedule-dates'),
  showDevicesBtn: document.getElementById('show-devices-btn'),
  devicesSection: document.getElementById('devices-section'),
  devicesList: document.getElementById('devices-list'),
  logoutBtn: document.getElementById('logout-btn'),

  // 공통
  messageArea: document.getElementById('message-area'),
  loading: document.getElementById('loading')
};

// ============================================
// 유틸리티 함수
// ============================================

/**
 * 로딩 표시/숨김
 */
function showLoading() {
  elements.loading.classList.remove('hidden');
}

function hideLoading() {
  elements.loading.classList.add('hidden');
}

/**
 * 메시지 표시
 */
function showMessage(message, type = 'info') {
  elements.messageArea.textContent = message;
  elements.messageArea.className = `message-area ${type}`;
  elements.messageArea.classList.remove('hidden');

  // 3초 후 자동 숨김
  setTimeout(() => {
    elements.messageArea.classList.add('hidden');
  }, 3000);
}

/**
 * 뷰 전환
 */
function showView(viewName) {
  elements.loginView.classList.add('hidden');
  elements.pendingView.classList.add('hidden');
  elements.mainView.classList.add('hidden');

  switch (viewName) {
    case 'login':
      elements.loginView.classList.remove('hidden');
      break;
    case 'pending':
      elements.pendingView.classList.remove('hidden');
      break;
    case 'main':
      elements.mainView.classList.remove('hidden');
      break;
  }
}

/**
 * 날짜 포맷팅
 */
function formatDate(dateString) {
  const date = new Date(dateString);
  return date.toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
}

// ============================================
// 에러 처리 함수
// ============================================

/**
 * API 에러 클래스
 */
class ApiError extends Error {
  constructor(code, message) {
    super(message);
    this.code = code;
  }
}

/**
 * HTTP 상태 코드를 에러 코드로 변환
 */
function getErrorCodeFromStatus(status) {
  if (status === 401) return ERROR_CODES.UNAUTHORIZED;
  if (status === 404) return ERROR_CODES.NOT_FOUND;
  if (status === 400) return ERROR_CODES.BAD_REQUEST;
  if (status >= 500) return ERROR_CODES.SERVER_ERROR;
  return ERROR_CODES.BAD_REQUEST;
}

/**
 * 에러 메시지 생성
 */
function getErrorMessage(code, serverMessage) {
  switch (code) {
    case ERROR_CODES.UNAUTHORIZED:
      return '인증 정보가 유효하지 않습니다. 다시 로그인해주세요.';
    case ERROR_CODES.NOT_FOUND:
      return '계정 정보를 찾을 수 없습니다. 다시 등록해주세요.';
    case ERROR_CODES.INVALID_STORAGE:
      return '저장된 정보가 손상되었습니다. 다시 로그인해주세요.';
    case ERROR_CODES.SERVER_ERROR:
      return '서버에 문제가 발생했습니다. 잠시 후 다시 시도해주세요.';
    case ERROR_CODES.NETWORK_ERROR:
      return '서버에 연결할 수 없습니다. 네트워크를 확인해주세요.';
    default:
      return serverMessage || '오류가 발생했습니다.';
  }
}

/**
 * 강제 로그아웃 처리
 */
async function forceLogout(message) {
  await clearStorage();
  elements.saveResult.classList.add('hidden');
  elements.devicesSection.classList.add('hidden');
  elements.emailInput.value = '';
  showView('login');
  showMessage(message, 'error');
}

/**
 * 공통 에러 핸들러
 * @returns {boolean} 에러가 처리되었으면 true
 */
async function handleApiError(error) {
  const code = error.code || ERROR_CODES.BAD_REQUEST;
  const message = getErrorMessage(code, error.message);

  if (LOGOUT_REQUIRED_ERRORS.includes(code)) {
    await forceLogout(message);
    return true;
  }

  showMessage(message, 'error');
  return false;
}

// ============================================
// 스토리지 함수
// ============================================

/**
 * 스토리지에서 데이터 가져오기
 */
async function getStorageData() {
  return await chrome.storage.local.get([
    CONFIG.STORAGE_KEYS.EMAIL,
    CONFIG.STORAGE_KEYS.IDENTIFIER,
    CONFIG.STORAGE_KEYS.IS_AUTHENTICATED
  ]);
}

/**
 * 스토리지에 데이터 저장
 */
async function setStorageData(data) {
  await chrome.storage.local.set(data);
}

/**
 * 스토리지 초기화
 */
async function clearStorage() {
  await chrome.storage.local.remove([
    CONFIG.STORAGE_KEYS.EMAIL,
    CONFIG.STORAGE_KEYS.IDENTIFIER,
    CONFIG.STORAGE_KEYS.IS_AUTHENTICATED
  ]);
}

/**
 * 스토리지 데이터 유효성 검증
 * - 인증 완료 상태인데 email 또는 identifier가 없으면 무효
 * @returns {boolean} 유효하면 true
 */
function isStorageDataValid(data) {
  // 인증 완료 상태라면 email과 identifier가 모두 있어야 함
  if (data.isAuthenticated) {
    return !!(data.email && data.identifier);
  }

  // 인증 대기 상태 (email, identifier 있고 isAuthenticated는 false)
  if (data.email && data.identifier) {
    return true;
  }

  // 미등록 상태 (모두 없거나 일부만 있음)
  // 일부만 있는 경우는 손상된 상태
  if (!data.email && !data.identifier && !data.isAuthenticated) {
    return true; // 정상적인 미등록 상태
  }

  return false; // 손상된 상태
}

/**
 * 인증이 필요한 작업 전 스토리지 검증
 * @throws {ApiError} 스토리지가 손상된 경우
 */
async function validateStorageForAuth() {
  const data = await getStorageData();

  if (!data.email || !data.identifier) {
    throw new ApiError(
      ERROR_CODES.INVALID_STORAGE,
      '저장된 인증 정보가 없습니다.'
    );
  }

  return data;
}

// ============================================
// API 함수
// ============================================

/**
 * API 요청 래퍼 (공통 에러 처리)
 */
async function apiRequest(url, options = {}) {
  let response;

  try {
    response = await fetch(url, options);
  } catch (error) {
    // 네트워크 오류 (서버 연결 실패, 타임아웃 등)
    throw new ApiError(ERROR_CODES.NETWORK_ERROR, error.message);
  }

  // 204 No Content인 경우 (DELETE 성공 등)
  if (response.status === 204) {
    return null;
  }

  let data;
  try {
    data = await response.json();
  } catch {
    data = {};
  }

  if (!response.ok) {
    const errorCode = getErrorCodeFromStatus(response.status);
    throw new ApiError(errorCode, data.message);
  }

  return data;
}

/**
 * 디바이스 등록 (회원가입)
 * - 400: 유효하지 않은 이메일 (로그아웃 불필요)
 */
async function registerDevice(email) {
  return await apiRequest(`${CONFIG.BASE_URL}/api/v1/members`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email })
  });
}

/**
 * 디바이스 목록 조회 (인증 확인용으로도 사용)
 * - 401: 인증되지 않은 디바이스 (로그아웃 필요)
 * - 404: 존재하지 않는 멤버/디바이스 (로그아웃 필요)
 */
async function getDevices(email, identifier) {
  const params = new URLSearchParams({ email, identifier });
  return await apiRequest(`${CONFIG.BASE_URL}/api/v1/members?${params}`);
}

/**
 * 디바이스 삭제
 * - 400: 소유자 불일치 (로그아웃 불필요)
 * - 401: 유효하지 않은 디바이스 (로그아웃 필요)
 */
async function deleteDevice(email, deviceIdentifier, targetDeviceIdentifier) {
  return await apiRequest(`${CONFIG.BASE_URL}/api/v1/device`, {
    method: 'DELETE',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      email,
      deviceIdentifier,
      targetDeviceIdentifier
    })
  });
}

/**
 * 복습 URL 저장
 * - 401/404: 유효하지 않은 디바이스 (로그아웃 필요)
 */
async function saveReviewUrl(identifier, targetUrl) {
  return await apiRequest(`${CONFIG.BASE_URL}/api/v1/reviews`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ identifier, targetUrl })
  });
}

// ============================================
// 이벤트 핸들러
// ============================================

/**
 * 디바이스 등록 버튼 클릭
 */
async function handleRegister() {
  const email = elements.emailInput.value.trim();

  if (!email) {
    showMessage('이메일을 입력해주세요.', 'error');
    return;
  }

  // 간단한 이메일 형식 검증
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(email)) {
    showMessage('유효한 이메일 형식이 아닙니다.', 'error');
    return;
  }

  try {
    showLoading();
    const result = await registerDevice(email);

    await setStorageData({
      [CONFIG.STORAGE_KEYS.EMAIL]: result.email,
      [CONFIG.STORAGE_KEYS.IDENTIFIER]: result.identifier,
      [CONFIG.STORAGE_KEYS.IS_AUTHENTICATED]: false
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
 * 인증 확인 버튼 클릭
 */
async function handleCheckAuth() {
  try {
    showLoading();
    const storageData = await validateStorageForAuth();

    const result = await getDevices(storageData.email, storageData.identifier);

    // 인증 성공
    await setStorageData({
      [CONFIG.STORAGE_KEYS.IS_AUTHENTICATED]: true
    });

    elements.userEmail.textContent = result.email;
    showView('main');
    showMessage('인증이 완료되었습니다!', 'success');
  } catch (error) {
    // 401은 인증 대기 중인 정상 상태
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
 * 다른 이메일로 등록 버튼 클릭
 */
async function handleReset() {
  await clearStorage();
  elements.emailInput.value = '';
  showView('login');
}

/**
 * URL 저장 버튼 클릭
 */
async function handleSaveUrl() {
  try {
    showLoading();

    // 현재 탭 URL 가져오기
    const [tab] = await chrome.tabs.query({ active: true, currentWindow: true });

    if (!tab?.url) {
      showMessage('현재 페이지의 URL을 가져올 수 없습니다.', 'error');
      return;
    }

    const storageData = await validateStorageForAuth();
    const result = await saveReviewUrl(storageData.identifier, tab.url);

    // 결과 표시
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
 * 디바이스 관리 버튼 클릭
 */
async function handleShowDevices() {
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
 * 디바이스 삭제 클릭
 */
async function handleDeleteDevice(targetIdentifier) {
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
    // 목록 새로고침 (숨겼다가 다시 열기)
    elements.devicesSection.classList.add('hidden');
    await handleShowDevices();
  } catch (error) {
    await handleApiError(error);
  } finally {
    hideLoading();
  }
}

/**
 * 로그아웃 버튼 클릭
 */
async function handleLogout() {
  if (!confirm('로그아웃 하시겠습니까?')) {
    return;
  }

  await clearStorage();
  elements.saveResult.classList.add('hidden');
  elements.devicesSection.classList.add('hidden');
  showView('login');
  showMessage('로그아웃 되었습니다.', 'info');
}

// ============================================
// 이벤트 리스너 등록
// ============================================

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

// ============================================
// 초기화
// ============================================

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
      // 인증 완료 상태
      elements.userEmail.textContent = storageData.email;
      showView('main');
    } else if (storageData.email && storageData.identifier) {
      // 등록 완료, 인증 대기
      elements.emailDisplay.textContent = storageData.email;
      showView('pending');
    } else {
      // 미등록 상태
      showView('login');
    }
  } catch (error) {
    console.error('초기화 오류:', error);
    await clearStorage();
    showView('login');
  }
}

// DOM 로드 후 초기화
document.addEventListener('DOMContentLoaded', initialize);