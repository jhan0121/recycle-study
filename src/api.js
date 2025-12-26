/**
 * API 호출 관련 함수
 */

import { CONFIG } from './config.js';
import { ERROR_CODES } from './constants.js';
import { ApiError, getErrorCodeFromStatus } from './errors.js';

/**
 * API 요청 래퍼 (공통 에러 처리)
 * @param {string} url - 요청 URL
 * @param {Object} options - fetch 옵션
 * @returns {Promise<Object|null>} 응답 데이터
 * @throws {ApiError}
 */
async function apiRequest(url, options = {}) {
  let response;

  try {
    response = await fetch(url, options);
  } catch (error) {
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
 * @param {string} email - 사용자 이메일
 * @returns {Promise<Object>} { email, identifier }
 */
export async function registerDevice(email) {
  return await apiRequest(`${CONFIG.BASE_URL}/api/v1/members`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email })
  });
}

/**
 * 디바이스 목록 조회
 * @param {string} email - 사용자 이메일
 * @param {string} identifier - 디바이스 식별자
 * @returns {Promise<Object>} { email, devices }
 */
export async function getDevices(email, identifier) {
  const params = new URLSearchParams({ email, identifier });
  return await apiRequest(`${CONFIG.BASE_URL}/api/v1/members?${params}`);
}

/**
 * 디바이스 삭제
 * @param {string} email - 사용자 이메일
 * @param {string} deviceIdentifier - 현재 디바이스 식별자
 * @param {string} targetDeviceIdentifier - 삭제할 디바이스 식별자
 */
export async function deleteDevice(email, deviceIdentifier, targetDeviceIdentifier) {
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
 * @param {string} identifier - 디바이스 식별자
 * @param {string} targetUrl - 저장할 URL
 * @returns {Promise<Object>} { url, scheduledAts }
 */
export async function saveReviewUrl(identifier, targetUrl) {
  return await apiRequest(`${CONFIG.BASE_URL}/api/v1/reviews`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ identifier, targetUrl })
  });
}