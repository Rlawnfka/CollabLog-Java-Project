const projects = [
  {
    name: "웹사이트 리뉴얼",
    description: "회사 홈페이지 디자인 및 기능 개선",
    progress: 75,
    createdAt: "2025-09-01"
  },
  {
    name: "마케팅 캠페인",
    description: "SNS 광고 및 콘텐츠 제작",
    progress: 40,
    createdAt: "2025-08-20"
  }
];

const container = document.getElementById('project-section');

function renderProjects() {
  container.innerHTML = ''; // 초기화

  if (projects.length === 0) {
    container.innerHTML = `
      <div class="empty-state">
        <div class="icon">📊</div>
        <h2>아직 생성한 프로젝트가 없습니다</h2>
        <p>첫 번째 프로젝트를 생성하여 업무를 추가하면 팀의 진행률과 성과를 확인할 수 있습니다.</p>
        <button class="create-btn">+ 첫 번째 프로젝트 만들기</button>
      </div>
    `;
  } else {
    const listHTML = projects.map(project => `
      <div class="project-card">
        <h3>${project.name}</h3>
        <p>${project.description}</p>
        <div class="meta">
          <span>생성일: ${project.createdAt}</span>
          <span>진행률: ${project.progress}%</span>
        </div>
        <div class="progress-bar">
          <div class="progress-fill" style="width: ${project.progress}%"></div>
        </div>
      </div>
    `).join('');

    container.innerHTML = `
      <div class="project-header">
        <h2>내 프로젝트</h2>
        <button class="create-btn">+ 새 프로젝트 만들기</button>
      </div>
      <div class="project-list">
        ${listHTML}
      </div>
    `;
  }
}

renderProjects();
