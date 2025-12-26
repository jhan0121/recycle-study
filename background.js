/**
 * Recycle Study Chrome Extension
 * 백그라운드 서비스 워커
 */

// ============================================
// 설정
// ============================================
const CONFIG = {
  BASE_URL: 'http://localhost:8080'
};

// ============================================
// 익스텐션 설치/업데이트 이벤트
// ============================================
chrome.runtime.onInstalled.addListener((details) => {
  if (details.reason === 'install') {
    console.log('Recycle Study 익스텐션이 설치되었습니다.');
  } else if (details.reason === 'update') {
    console.log('Recycle Study 익스텐션이 업데이트되었습니다.');
  }
});

// ============================================
// 메시지 리스너 (popup.js와 통신용)
// ============================================
chrome.runtime.onMessage.addListener((message, sender, sendResponse) => {
  // 비동기 응답을 위해 true 반환
  if (message.type === 'CHECK_AUTH') {
    // 향후 백그라운드에서 인증 상태 확인 로직 추가 가능
    sendResponse({ success: true });
  }

  return true; // 비동기 응답 허용
});
