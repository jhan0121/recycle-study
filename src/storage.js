/**
 * Chrome Storage 관련 함수
 *
 * 로컬 스토리지를 통해 이메일, 디바이스 식별자, 인증 상태를 저장하고 관리한다.
 */

import { STORAGE_KEYS } from './config.js';
import { ERROR_CODES } from './constants.js';
import { ApiError } from './errors.js';

/**
 * 스토리지에서 데이터 가져오기
 * @returns {Promise<Object>} 저장된 데이터
 */
export async function getStorageData() {
  return await chrome.storage.local.get([
    STORAGE_KEYS.EMAIL,
    STORAGE_KEYS.IDENTIFIER,
    STORAGE_KEYS.IS_AUTHENTICATED
  ]);
}

/**
 * 스토리지에 데이터 저장
 * @param {Object} data - 저장할 데이터
 */
export async function setStorageData(data) {
  await chrome.storage.local.set(data);
}

/**
 * 스토리지 초기화
 */
export async function clearStorage() {
  await chrome.storage.local.remove([
    STORAGE_KEYS.EMAIL,
    STORAGE_KEYS.IDENTIFIER,
    STORAGE_KEYS.IS_AUTHENTICATED
  ]);
}

/**
 * 스토리지 데이터 유효성 검증
 * @param {Object} data - 검증할 데이터
 * @returns {boolean} 유효하면 true
 */
export function isStorageDataValid(data) {
  // 인증 완료 상태라면 email과 identifier가 모두 있어야 함
  if (data.isAuthenticated) {
    return !!(data.email && data.identifier);
  }

  // 인증 대기 상태 (email, identifier 있고 isAuthenticated는 false)
  if (data.email && data.identifier) {
    return true;
  }

  // 미등록 상태 (모두 없으면 정상)
  if (!data.email && !data.identifier && !data.isAuthenticated) {
    return true;
  }

  // 일부만 있는 경우는 손상된 상태
  return false;
}

/**
 * 인증이 필요한 작업 전 스토리지 검증
 * @throws {ApiError} 스토리지가 손상된 경우
 * @returns {Promise<Object>} 검증된 스토리지 데이터
 */
export async function validateStorageForAuth() {
  const data = await getStorageData();

  if (!data.email || !data.identifier) {
    throw new ApiError(
      ERROR_CODES.INVALID_STORAGE,
      '저장된 인증 정보가 없습니다.'
    );
  }

  return data;
}
