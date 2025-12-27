/**
 * 유틸리티 함수
 *
 * 날짜 포맷팅, 이메일 검증 등 범용 헬퍼 함수를 정의한다.
 */

/**
 * 날짜 포맷팅
 * @param {string} dateString - ISO 형식 날짜 문자열
 * @returns {string} 포맷된 날짜 문자열
 */
export function formatDate(dateString) {
  const date = new Date(dateString);
  return date.toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
}

/**
 * 이메일 형식 검증
 * @param {string} email - 검증할 이메일
 * @returns {boolean} 유효하면 true
 */
export function isValidEmail(email) {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email);
}