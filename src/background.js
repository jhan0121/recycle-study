/**
 * 백그라운드 서비스 워커
 *
 * 익스텐션 설치/업데이트 이벤트 처리 및 popup과의 메시지 통신을 담당한다.
 * API 요청은 CORS 우회를 위해 이 서비스 워커에서 처리한다.
 */

import { CONFIG } from './config.js';

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
// API 프록시 핸들러
// ============================================
async function handleApiRequest(request) {
  const { endpoint, method = 'GET', body, params } = request;

  let url = `${CONFIG.BASE_URL}${endpoint}`;
  if (params) {
    url += `?${new URLSearchParams(params)}`;
  }

  const options = {
    method,
    headers: { 'Content-Type': 'application/json' }
  };

  if (body) {
    options.body = JSON.stringify(body);
  }

  try {
    const response = await fetch(url, options);

    // 204 No Content
    if (response.status === 204) {
      return { success: true, data: null };
    }

    let data;
    try {
      data = await response.json();
    } catch (parseError) {
      console.error('Failed to parse JSON response:', parseError);
      data = { message: 'Invalid JSON response from server.' };
    }

    if (!response.ok) {
      console.error('API request failed:', { url, status: response.status, data });
      return { success: false, status: response.status, message: data.message };
    }

    return { success: true, data };
  } catch (error) {
    console.error('Network request failed:', { url, error });
    return { success: false, status: 0, message: error.message, isNetworkError: true };
  }
}

// ============================================
// 메시지 리스너 (popup과 통신)
// ============================================
chrome.runtime.onMessage.addListener((message, sender, sendResponse) => {
  if (message.type === 'API_REQUEST') {
    handleApiRequest(message.request)
      .then(sendResponse);
    return true; // 비동기 응답을 위해 true 반환
  }

  if (message.type === 'CHECK_AUTH') {
    sendResponse({ success: true });
  }
});
