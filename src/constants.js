/**
 * 에러 코드 정의
 */
export const ERROR_CODES = {
  // 로그아웃이 필요한 에러
  UNAUTHORIZED: 'UNAUTHORIZED',           // 401: 인증되지 않은 디바이스
  NOT_FOUND: 'NOT_FOUND',                 // 404: 존재하지 않는 리소스
  INVALID_STORAGE: 'INVALID_STORAGE',     // 스토리지 데이터 손상

  // 로그아웃 불필요한 에러
  BAD_REQUEST: 'BAD_REQUEST',             // 400: 잘못된 요청
  SERVER_ERROR: 'SERVER_ERROR',           // 5xx: 서버 오류
  NETWORK_ERROR: 'NETWORK_ERROR'          // 네트워크 연결 실패
};

/**
 * 자동 로그아웃이 필요한 에러 코드
 */
export const LOGOUT_REQUIRED_ERRORS = [
  ERROR_CODES.UNAUTHORIZED,
  ERROR_CODES.NOT_FOUND,
  ERROR_CODES.INVALID_STORAGE
];