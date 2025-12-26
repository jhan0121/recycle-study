/**
 * 환경 설정
 *
 * 개발: BASE_URL을 localhost로 설정
 * 프로덕션: BASE_URL을 실제 서버 주소로 변경
 */
export const CONFIG = {
  BASE_URL: 'http://localhost:8080',
  ENV: 'development'
};

export const STORAGE_KEYS = {
  EMAIL: 'email',
  IDENTIFIER: 'identifier',
  IS_AUTHENTICATED: 'isAuthenticated'
};
