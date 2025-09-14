document.getElementById('loginForm').addEventListener('submit', function (e) {
  e.preventDefault();

  const username = document.getElementById('username').value.trim();
  const password = document.getElementById('password').value.trim();

  if (username && password) {
    alert(`로그인 시도: ${username}`);
    // 실제 로그인 로직은 서버와 연동 필요
  } else {
    alert('모든 필드를 입력해주세요.');
  }
});