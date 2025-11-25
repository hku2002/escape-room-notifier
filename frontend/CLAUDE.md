# CLAUDE.md - Frontend

이 파일은 Claude Code (claude.ai/code)가 프론트엔드 코드 작업 시 참고해야 할 가이드입니다.

## 프로젝트 개요

방탈출 예약 알림 서비스의 프론트엔드 애플리케이션입니다.

**기술 스택**: React 18, TypeScript, Vite 7

## 개발 환경 요구사항

- Node.js: v20.19+ 또는 v22.12+
- npm: v10+

**중요**: Node.js 18.x는 지원하지 않습니다. `nvm use 22`로 버전을 변경하세요.

## 자주 사용하는 명령어

### 의존성 설치
```bash
npm install
```

### 개발 서버 실행
```bash
# Node.js 22 사용 (필수)
source ~/.nvm/nvm.sh
nvm use 22

# 개발 서버 시작
npm run dev
```

개발 서버 URL: http://localhost:5173

### 빌드
```bash
# 프로덕션 빌드
npm run build

# 빌드 결과 미리보기
npm run preview
```

### 린트
```bash
# ESLint 실행
npm run lint
```

## 테스트 및 애플리케이션 종료 규칙 (중요)

**반드시 준수해야 할 규칙:**

1. **개발 서버 테스트 후 반드시 종료**
   - 포트 5173이 계속 사용 중이면 다음 실행 시 오류 발생
   - 백그라운드로 실행한 경우 특히 주의

2. **종료 명령어:**
   ```bash
   # 5173 포트 프로세스 확인 및 종료
   lsof -ti:5173 | xargs kill -9

   # 또는 개발 서버 실행 터미널에서 Ctrl+C
   ```

3. **개발 완료 후 체크리스트:**
   - [ ] 개발 서버 종료 확인
   - [ ] 포트 5173 사용 중인 프로세스 없음 확인: `lsof -ti:5173`
   - [ ] 불필요한 console.log 제거
   - [ ] ESLint 경고 확인: `npm run lint`

## API 연동

백엔드 API Base URL은 환경 변수로 관리합니다:

- **개발 환경**: `.env.development`
  ```
  VITE_API_BASE_URL=http://localhost:8080
  ```

- **프로덕션 환경**: `.env.production`
  ```
  VITE_API_BASE_URL=http://localhost:8080
  ```

### API 사용 예시
```typescript
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

const response = await fetch(`${API_BASE_URL}/api/user-alerts`);
```

## 프로젝트 구조

```
frontend/
├── src/
│   ├── components/     # React 컴포넌트
│   ├── pages/          # 페이지 컴포넌트
│   ├── services/       # API 호출 로직
│   ├── types/          # TypeScript 타입 정의
│   ├── utils/          # 유틸리티 함수
│   ├── App.tsx         # 메인 App 컴포넌트
│   └── main.tsx        # 진입점
├── public/             # 정적 파일
├── .env.development    # 개발 환경 변수
├── .env.production     # 프로덕션 환경 변수
├── vite.config.ts      # Vite 설정
└── tsconfig.json       # TypeScript 설정
```

## 개발 가이드라인

### 컴포넌트 작성
- 함수형 컴포넌트 사용
- React Hooks 활용 (useState, useEffect, useMemo 등)
- Props 타입은 TypeScript interface로 명시

### 스타일링
- CSS Modules 또는 Styled Components 권장
- 반응형 디자인 필수 (모바일 우선)
- 다크/라이트 모드 지원 고려

### 상태 관리
- 간단한 상태: useState
- 복잡한 상태: Context API 또는 Zustand
- 서버 상태: React Query 권장

### API 호출
- async/await 사용
- 에러 핸들링 필수
- 로딩 상태 관리

### 코드 품질
- ESLint 규칙 준수
- TypeScript strict 모드 사용
- 의미 있는 변수/함수 이름 사용
- 주석은 최소화 (코드가 스스로 설명하도록)

## 빌드 및 배포

### 빌드
```bash
npm run build
```

빌드 결과는 `dist/` 디렉토리에 생성됩니다.

### 배포 옵션
- **Vercel**: 가장 간단한 배포 방법
- **Netlify**: Git 연동 자동 배포
- **AWS S3 + CloudFront**: 정적 호스팅

### 환경 변수 설정
프로덕션 배포 시 `.env.production`의 `VITE_API_BASE_URL`을 실제 백엔드 URL로 변경하세요.

## 문제 해결

### Node.js 버전 오류
```
Error: Vite requires Node.js version 20.19+ or 22.12+
```
**해결**: `nvm use 22` 명령어로 Node.js 22 사용

### 포트 이미 사용 중
```
Port 5173 is already in use
```
**해결**: `lsof -ti:5173 | xargs kill -9`로 프로세스 종료

### CORS 에러
백엔드의 `WebConfig.java`에서 프론트엔드 URL이 허용되었는지 확인하세요.
