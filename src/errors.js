/**
 * 에러 처리 관련 함수
 */

import { ERROR_CODES, LOGOUT_REQUIRED_ERRORS } from './constants.js';

/**
 * API 에러 클래스
 */
export class ApiError extends Error {
  constructor(code, message) {
    super(message);
    this.code = code;
    this.name = 'ApiError';
  }
}

/**
 * HTTP 상태 코드를 에러 코드로 변환
 * @param {number} status - HTTP 상태 코드
 * @returns {string} 에러 코드
 */
export function getErrorCodeFromStatus(status) {
  if (status === 401) return ERROR_CODES.UNAUTHORIZED;
  if (status === 404) return ERROR_CODES.NOT_FOUND;
  if (status === 400) return ERROR_CODES.BAD_REQUEST;
  if (status >= 500) return ERROR_CODES.SERVER_ERROR;
  return ERROR_CODES.BAD_REQUEST;
}

/**
 * 에러 코드에 따른 사용자 메시지 생성
 * @param {string} code - 에러 코드
 * @param {string} serverMessage - 서버에서 받은 메시지
 * @returns {string} 사용자에게 표시할 메시지
 */
export function getErrorMessage(code, serverMessage) {
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
 * 로그아웃이 필요한 에러인지 확인
 * @param {string} code - 에러 코드
 * @returns {boolean}
 */
export function isLogoutRequiredError(code) {
  return LOGOUT_REQUIRED_ERRORS.includes(code);
}
