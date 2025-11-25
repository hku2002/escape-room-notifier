# CLAUDE.md

이 파일은 Claude Code (claude.ai/code)가 이 저장소에서 작업할 때 필요한 가이드를 제공합니다.

## 프로젝트 개요

방탈출 카페의 예약 가능 여부를 모니터링하고, 원하는 시간대에 빈 자리가 생기면 사용자에게 알림을 보내는 풀스택 웹 애플리케이션입니다. 방탈출 예약 사이트를 크롤링하여 예약 가능 정보를 수집하고, 사용자 설정과 비교하여 실시간 알림을 제공합니다.

**기술 스택**:
- Backend: Java 17, Spring Boot 3.5.9, Spring Data JPA, Spring Web, H2 Database (개발용), Lombok
- Frontend: React 18, TypeScript, Vite 7

**프로젝트 구조**:
- `/src` - 백엔드 소스 코드 (Spring Boot)
- `/frontend` - 프론트엔드 소스 코드 (React + Vite)

**기본 패키지**: `kr.co.escape.api`

## 자주 사용하는 명령어

### 빌드 및 실행
```bash
# 프로젝트 빌드
./gradlew build

# 애플리케이션 실행
./gradlew bootRun

# 테스트 실행
./gradlew test

# 특정 테스트 클래스 실행
./gradlew test --tests "ClassName"

# 특정 테스트 메서드 실행
./gradlew test --tests "ClassName.methodName"

# 클린 빌드
./gradlew clean build
```

### 개발
```bash
# 의존성 업데이트 확인
./gradlew dependencyUpdates

# 프로젝트 리포트 생성
./gradlew check
```

### 프론트엔드 (React + Vite)
```bash
# 프론트엔드 디렉토리로 이동
cd frontend

# 의존성 설치 (최초 1회)
npm install

# 개발 서버 실행 (포트: 5173)
npm run dev

# 프로덕션 빌드
npm run build

# 빌드 결과 미리보기
npm run preview
```

### 전체 애플리케이션 실행 (개발 모드)
```bash
# 터미널 1: 백엔드 실행
./gradlew bootRun

# 터미널 2: 프론트엔드 실행
cd frontend && npm run dev
```

**개발 서버 URL**:
- 백엔드: http://localhost:8080
- 프론트엔드: http://localhost:5173
- H2 Console: http://localhost:8080/h2-console

### H2 데이터베이스 접근
애플리케이션 실행 후 http://localhost:8080/h2-console 접속
- JDBC URL: `jdbc:h2:mem:escaperoom`
- Username: `sa`
- Password: (공백)

**중요**: 애플리케이션 테스트 후 반드시 종료할 것
```bash
# 8080 포트 프로세스 확인 및 종료
lsof -ti:8080 | xargs kill -9
```

## 아키텍처

### 시스템 컴포넌트

**크롤링 시스템**: 다양한 방탈출 예약 플랫폼에서 예약 가능 정보를 수집하는 스케줄러 기반 크롤러입니다. JavaScript로 렌더링되는 동적 웹페이지를 처리하고, 5-30분 간격의 Rate Limit을 준수해야 합니다.

**사용자 알림 관리**: 사용자가 원하는 테마, 날짜 범위, 시간대, 인원 수, 알림 방식을 설정합니다. 시스템은 새로운 예약 가능 정보를 이 조건과 비교하여 일치 여부를 확인합니다.

**알림 엔진**: 사용자 조건과 일치하는 예약 가능 정보가 발견되면, 설정된 채널(이메일, 카카오톡, 푸시)을 통해 알림을 발송합니다. 동일한 시간대에 대한 중복 알림을 방지하는 로직을 포함합니다.

**데이터 비교 엔진**: 새로 크롤링한 예약 가능 정보를 이전 상태와 비교하여 변경사항을 감지합니다. Redis를 사용하여 캐싱하고 중복 알림을 방지합니다.

### 데이터베이스 스키마

현재 구현된 Entity:
- **User** (`kr.co.escape.api.entity.User`): 사용자 계정 및 알림 설정
- **Theme** (`kr.co.escape.api.entity.Theme`): 방탈출 테마 정보 (카페명, 테마명, 지점, 난이도, 장르, 소요시간, 가격)
- **UserAlert** (`kr.co.escape.api.entity.UserAlert`): 사용자 알림 설정 (테마, 날짜 범위, 선호 시간대, 인원 수, 활성화 상태)
- **Availability** (`kr.co.escape.api.entity.Availability`): 크롤링한 예약 가능 시간대 (테마, 날짜, 시간, 예약 가능 여부, 크롤링 시간)
- **Notifications** (`kr.co.escape.api.entity.Notification`): 알림 히스토리 (사용자, 알림 설정, 예약 가능 정보, 발송 시간, 알림 타입, 상태)

주요 연관관계:
- User ↔ UserAlert (1:N)
- User ↔ Notification (1:N)
- Theme ↔ UserAlert (1:N)
- Theme ↔ Availability (1:N)
- UserAlert ↔ Notification (1:N)
- Availability ↔ Notification (1:N)

### 주요 설계 고려사항

**크롤링 전략**:
- JavaScript 기반 사이트는 Selenium/Playwright 사용, 정적 HTML은 Jsoup 사용
- 크롤링 실패 시 exponential backoff를 사용한 재시도 로직 구현
- robots.txt 준수 및 필요시 User-Agent 로테이션 구현
- CAPTCHA 처리 전략 필요

**성능 최적화**:
- 비동기/반응형 크롤링 작업을 위해 Spring WebFlux 사용
- 자주 조회되는 데이터는 Redis에 캐싱
- 가능한 경우 데이터베이스 배치 작업 수행
- 여러 사이트 병렬 크롤링을 위한 멀티스레딩 고려

**알림 중복 방지**:
- Redis에 발송된 알림 키 저장 (user_id + theme_id + datetime)
- TTL 기반 만료로 알림 추적 관리
- 설정 가능한 시간 내 동일 시간대 재알림 방지

**확장성**:
- 새로운 예약 플랫폼 추가를 위한 추상 크롤러 인터페이스 설계
- 플러그인 방식의 알림 채널 구조
- 사이트별 크롤링 스케줄 설정 가능

## 개발 가이드라인

**Entity 연관관계**: JPA 양방향 관계를 신중하게 사용하세요. User ↔ UserAlert, Theme ↔ Availability가 주요 관계입니다. 성능을 위해 Fetch 전략(LAZY vs EAGER)을 고려하세요.

**스케줄러 설정**: 크롤러에 `@Scheduled` 어노테이션을 사용하세요. application.yml에서 사이트별로 다른 크롤링 간격을 위한 cron 표현식을 설정하세요.

**에러 처리**: 크롤링 실패는 로그를 남기되 애플리케이션을 중단시키지 않아야 합니다. 반복적으로 실패하는 사이트에 대해 Circuit Breaker 패턴을 구현하세요.

**테스트 및 애플리케이션 종료 규칙**:
- API 또는 기능 테스트 후 **반드시** 애플리케이션을 종료해야 합니다
- 8080 포트가 계속 사용 중이면 다음 실행 시 오류가 발생합니다
- 종료 명령어: `lsof -ti:8080 | xargs kill -9`
- 백그라운드로 실행한 경우, 테스트 완료 후 즉시 프로세스를 종료하세요
- Mock HTTP 응답을 사용한 크롤러 통합 테스트를 작성하세요
- 프로덕션 배포 전에 테스트 채널로 알림 로직을 검증하세요
- 테스트 데이터는 `DataInitializer` 클래스에서 자동으로 초기화됩니다

## 설정

`src/main/resources/application.yml`에 다음 설정이 포함되어야 합니다:

**현재 설정**:
- H2 in-memory 데이터베이스 (개발용)
- H2 Console 활성화 (/h2-console)
- JPA ddl-auto: create-drop (개발 모드)
- SQL 로깅 활성화

**향후 필요한 설정**:
- 프로덕션 데이터베이스 연결 (MariaDB/MySQL)
- Redis 캐싱 설정
- SMTP 이메일 설정
- 카카오톡 API 인증 정보
- 크롤러 User-Agent 및 타임아웃 설정
- Rate Limiting 파라미터

## 법적/윤리적 고려사항

**크롤링 준수사항**:
- 크롤링 전 반드시 robots.txt 확인
- 서버 과부하 방지를 위한 Rate Limiting 구현
- 접근 제어를 우회하지 말 것
- 각 사이트의 이용약관에서 크롤링 허용 여부 확인

**데이터 프라이버시**:
- BCrypt를 사용한 사용자 비밀번호 암호화
- 알림 설정 정보의 안전한 저장
- 모든 외부 통신에 HTTPS 필수
- GDPR/국내 개인정보보호법 준수