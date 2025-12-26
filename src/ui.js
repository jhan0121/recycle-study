/**
 * UI 관련 함수
 */

import { clearStorage } from './storage.js';
import { getErrorMessage, isLogoutRequiredError } from './errors.js';
import { ERROR_CODES } from './constants.js';

/**
 * DOM 요소 캐시
 */
export const elements = {
  // 뷰
  loginView: null,
  pendingView: null,
  mainView: null,

  // 로그인 화면
  emailInput: null,
  registerBtn: null,

  // 인증 대기 화면
  emailDisplay: null,
  checkAuthBtn: null,
  resetBtn: null,

  // 메인 화면
  userEmail: null,
  saveUrlBtn: null,
  saveResult: null,
  scheduleDates: null,
  showDevicesBtn: null,
  devicesSection: null,
  devicesList: null,
  logoutBtn: null,

  // 공통
  messageArea: null,
  loading: null
};

/**
 * DOM 요소 초기화
 */
export function initializeElements() {
  elements.loginView = document.getElementById('login-view');
  elements.pendingView = document.getElementById('pending-view');
  elements.mainView = document.getElementById('main-view');

  elements.emailInput = document.getElementById('email-input');
  elements.registerBtn = document.getElementById('register-btn');

  elements.emailDisplay = document.querySelector('.email-display');
  elements.checkAuthBtn = document.getElementById('check-auth-btn');
  elements.resetBtn = document.getElementById('reset-btn');

  elements.userEmail = document.getElementById('user-email');
  elements.saveUrlBtn = document.getElementById('save-url-btn');
  elements.saveResult = document.getElementById('save-result');
  elements.scheduleDates = document.getElementById('schedule-dates');
  elements.showDevicesBtn = document.getElementById('show-devices-btn');
  elements.devicesSection = document.getElementById('devices-section');
  elements.devicesList = document.getElementById('devices-list');
  elements.logoutBtn = document.getElementById('logout-btn');

  elements.messageArea = document.getElementById('message-area');
  elements.loading = document.getElementById('loading');
}

/**
 * 로딩 표시
 */
export function showLoading() {
  elements.loading.classList.remove('hidden');
}

/**
 * 로딩 숨김
 */
export function hideLoading() {
  elements.loading.classList.add('hidden');
}

/**
 * 메시지 표시
 * @param {string} message - 표시할 메시지
 * @param {string} type - 메시지 타입 ('info' | 'success' | 'error')
 */
export function showMessage(message, type = 'info') {
  elements.messageArea.textContent = message;
  elements.messageArea.className = `message-area ${type}`;
  elements.messageArea.classList.remove('hidden');

  setTimeout(() => {
    elements.messageArea.classList.add('hidden');
  }, 3000);
}

/**
 * 뷰 전환
 * @param {string} viewName - 표시할 뷰 ('login' | 'pending' | 'main')
 */
export function showView(viewName) {
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
 * 강제 로그아웃 처리
 * @param {string} message - 표시할 메시지
 */
export async function forceLogout(message) {
  await clearStorage();
  elements.saveResult.classList.add('hidden');
  elements.devicesSection.classList.add('hidden');
  elements.emailInput.value = '';
  showView('login');
  showMessage(message, 'error');
}

/**
 * 공통 API 에러 핸들러
 * @param {Error} error - 에러 객체
 * @returns {Promise<boolean>} 로그아웃되었으면 true
 */
export async function handleApiError(error) {
  const code = error.code || ERROR_CODES.BAD_REQUEST;
  const message = getErrorMessage(code, error.message);

  if (isLogoutRequiredError(code)) {
    await forceLogout(message);
    return true;
  }

  showMessage(message, 'error');
  return false;
}
