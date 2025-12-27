/**
 * 환경 설정
 *
 * 개발: vite build --mode dev (.env.dev 사용)
 * 프로덕션: vite build --mode prod (.env.prod 사용)
 */
export const CONFIG = {
  BASE_URL: import.meta.env.VITE_BASE_URL || 'http://localhost:8080',
  ENV: import.meta.env.MODE || 'development'
};

export const STORAGE_KEYS = {
  EMAIL: 'email',
  IDENTIFIER: 'identifier',
  IS_AUTHENTICATED: 'isAuthenticated'
};
