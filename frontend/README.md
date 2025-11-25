# 방탈출 예약 알림 서비스 - Frontend

방탈출 카페의 예약 가능 여부를 실시간으로 확인하고 알림을 받을 수 있는 웹 애플리케이션의 프론트엔드입니다.

## 기술 스택

- **React 18** - UI 라이브러리
- **TypeScript** - 타입 안정성
- **Vite 7** - 빌드 도구 및 개발 서버
- **ESLint** - 코드 품질 관리

## 시작하기

### 필수 요구사항

- Node.js v20.19+ 또는 v22.12+
- npm v10+

**중요**: Node.js 18.x는 Vite 7에서 지원하지 않습니다.

### Node.js 버전 설정

nvm을 사용하는 경우:

```bash
# Node.js 22 설치
nvm install 22

# Node.js 22 사용
nvm use 22

# 버전 확인
node --version  # v22.x.x 출력되어야 함
```

### 설치 및 실행

```bash
# 의존성 설치
npm install

# 개발 서버 실행
npm run dev
```

개발 서버가 시작되면 브라우저에서 http://localhost:5173 으로 접속하세요.

## 개발 명령어

### 개발 서버 실행
```bash
npm run dev
```
- HMR(Hot Module Replacement) 지원
- 포트: 5173

### 프로덕션 빌드
```bash
npm run build
```
- 최적화된 빌드 생성
- 결과물: `dist/` 디렉토리

### 빌드 미리보기
```bash
npm run preview
```
- 빌드된 결과물을 로컬에서 테스트

### 린트 실행
```bash
npm run lint
```
- ESLint로 코드 품질 검사

## 환경 변수

### 개발 환경 (`.env.development`)
```
VITE_API_BASE_URL=http://localhost:8080
```

### 프로덕션 환경 (`.env.production`)
```
VITE_API_BASE_URL=https://your-api-domain.com
```

환경 변수는 `import.meta.env.VITE_API_BASE_URL`로 접근할 수 있습니다.

## 프로젝트 구조

```
frontend/
├── public/             # 정적 파일
│   └── vite.svg
├── src/
│   ├── assets/        # 이미지, 폰트 등
│   ├── components/    # 재사용 가능한 컴포넌트
│   ├── pages/         # 페이지 컴포넌트
│   ├── services/      # API 호출 로직
│   ├── types/         # TypeScript 타입 정의
│   ├── utils/         # 유틸리티 함수
│   ├── App.tsx        # 메인 App 컴포넌트
│   ├── App.css        # App 스타일
│   ├── main.tsx       # 진입점
│   └── index.css      # 전역 스타일
├── .env.development   # 개발 환경 변수
├── .env.production    # 프로덕션 환경 변수
├── eslint.config.js   # ESLint 설정
├── tsconfig.json      # TypeScript 설정
├── vite.config.ts     # Vite 설정
└── package.json       # 프로젝트 메타데이터
```

## 백엔드 연동

백엔드 API는 Spring Boot로 개발되어 있으며, 기본적으로 http://localhost:8080 에서 실행됩니다.

### API 엔드포인트 예시

```typescript
// 사용자 알림 목록 조회
GET /api/user-alerts

// 응답 예시
{
  "success": true,
  "data": [
    {
      "alertId": 1,
      "isActive": true,
      "theme": {
        "themeId": 1,
        "themeName": "더 지니어스",
        "cafeName": "넥스트에디션",
        "branchName": "강남점"
      },
      "dateStart": "2025-12-01",
      "dateEnd": "2025-12-31",
      "preferredTimes": ["18:00", "19:00", "20:00"],
      "numPeople": 4
    }
  ]
}
```

## 개발 가이드

### 컴포넌트 작성

```tsx
// src/components/AlertCard.tsx
import { FC } from 'react';

interface AlertCardProps {
  themeName: string;
  cafeName: string;
  isActive: boolean;
}

const AlertCard: FC<AlertCardProps> = ({ themeName, cafeName, isActive }) => {
  return (
    <div className="alert-card">
      <h3>{themeName}</h3>
      <p>{cafeName}</p>
      <span>{isActive ? '🔔 활성' : '🔕 비활성'}</span>
    </div>
  );
};

export default AlertCard;
```

### API 호출

```tsx
// src/services/api.ts
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

export const getUserAlerts = async () => {
  const response = await fetch(`${API_BASE_URL}/api/user-alerts`);
  if (!response.ok) {
    throw new Error('Failed to fetch user alerts');
  }
  return response.json();
};
```

## 배포

### Vercel 배포 (권장)

1. Vercel 계정 생성 (https://vercel.com)
2. GitHub 저장소 연결
3. 환경 변수 설정: `VITE_API_BASE_URL`
4. 자동 배포 완료

### Netlify 배포

1. Netlify 계정 생성 (https://netlify.com)
2. GitHub 저장소 연결
3. Build command: `npm run build`
4. Publish directory: `dist`
5. 환경 변수 설정: `VITE_API_BASE_URL`

## 문제 해결

### Node.js 버전 오류

```
Error: Vite requires Node.js version 20.19+ or 22.12+
```

**해결**: `nvm use 22` 명령어로 Node.js 버전 변경

### 포트 충돌

```
Port 5173 is already in use
```

**해결**:
```bash
# 프로세스 찾기 및 종료
lsof -ti:5173 | xargs kill -9
```

### CORS 에러

백엔드 서버(`localhost:8080`)가 실행 중인지 확인하고, 백엔드의 CORS 설정에서 `http://localhost:5173`이 허용되었는지 확인하세요.

## 참고 자료

- [React 공식 문서](https://react.dev)
- [Vite 공식 문서](https://vite.dev)
- [TypeScript 공식 문서](https://www.typescriptlang.org)

## 라이선스

이 프로젝트는 개인 프로젝트입니다.
