/**
 * API 호출 관련 함수
 *
 * 서버와의 통신을 담당하며, 디바이스 등록/조회/삭제, 복습 URL 저장 등의 API를 제공한다.
 * 모든 API 요청은 background.js를 통해 처리되어 CORS 문제를 우회한다.
 */

import { ERROR_CODES } from './constants.js';
import { ApiError, getErrorCodeFromStatus } from './errors.js';

/**
 * Background Script로 API 요청 전송
 * @param {Object} request - API 요청 정보
 * @returns {Promise<Object|null>} 응답 데이터
 * @throws {ApiError}
 */
async function sendApiRequest(request) {
  const response = await chrome.runtime.sendMessage({
    type: 'API_REQUEST',
    request
  });

  if (!response.success) {
    if (response.isNetworkError) {
      throw new ApiError(ERROR_CODES.NETWORK_ERROR, response.message);
    }
    const errorCode = getErrorCodeFromStatus(response.status);
    throw new ApiError(errorCode, response.message);
  }

  return response.data;
}

/**
 * 디바이스 등록 (회원가입)
 * @param {string} email - 사용자 이메일
 * @returns {Promise<Object>} { email, identifier }
 */
export async function registerDevice(email) {
  return await sendApiRequest({
    endpoint: '/api/v1/members',
    method: 'POST',
    body: { email }
  });
}

/**
 * 디바이스 목록 조회
 * @param {string} email - 사용자 이메일
 * @param {string} identifier - 디바이스 식별자
 * @returns {Promise<Object>} { email, devices }
 */
export async function getDevices(email, identifier) {
  return await sendApiRequest({
    endpoint: '/api/v1/members',
    method: 'GET',
    params: { email, identifier }
  });
}

/**
 * 디바이스 삭제
 * @param {string} email - 사용자 이메일
 * @param {string} deviceIdentifier - 현재 디바이스 식별자
 * @param {string} targetDeviceIdentifier - 삭제할 디바이스 식별자
 */
export async function deleteDevice(email, deviceIdentifier, targetDeviceIdentifier) {
  return await sendApiRequest({
    endpoint: '/api/v1/device',
    method: 'DELETE',
    body: { email, deviceIdentifier, targetDeviceIdentifier }
  });
}

/**
 * 복습 URL 저장
 * @param {string} identifier - 디바이스 식별자
 * @param {string} targetUrl - 저장할 URL
 * @returns {Promise<Object>} { url, scheduledAts }
 */
export async function saveReviewUrl(identifier, targetUrl) {
  return await sendApiRequest({
    endpoint: '/api/v1/reviews',
    method: 'POST',
    body: { identifier, targetUrl }
  });
}
