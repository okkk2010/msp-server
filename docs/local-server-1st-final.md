# MSP Server Local 1st Final

## 1. 문서 목적

이 문서는 `msp-server`의 현재 구현 상태를 기준으로 로컬 1차 완료 범위를 정리한다.
기준 시점은 서버 저장소에 반영된 Spring Boot 구현이며, 프론트/윈도우 클라이언트 연동 시 참고할 로컬 기준 문서로 사용한다.

## 2. 현재 구현 범위

- Spring Boot 기반 Backend API 구성
- PostgreSQL + Flyway 기반 DB 초기화
- Google OAuth 로그인 시작 및 JWT 발급/갱신/로그아웃
- Platform / Game 조회 API
- Overlay 목록 / 상세 / 코드 조회 API
- Overlay 업로드 / 수정 / 삭제 API
- overlay.json JSON Schema 검증
- overlay.json / thumbnail.png 스토리지 저장
- User Library 저장 / 조회 / 삭제 API
- Swagger/OpenAPI 문서화

## 3. 기술 스택

- Java 17
- Spring Boot 4.0.5
- Spring Web
- Spring Validation
- Spring Data JPA
- Spring Security
- Spring OAuth2 Client
- PostgreSQL
- Flyway
- springdoc OpenAPI

## 4. 로컬 실행 기준

### 4.1 기본 포트

- Backend: `http://localhost:8080`
- Frontend 허용 기본 Origin: `http://localhost:5173`
- PostgreSQL: `localhost:5433`

### 4.2 주요 설정 파일

- [application.yml](/d:/project/msp/msp-server/src/main/resources/application.yml:1)
- [application-local.yml](/d:/project/msp/msp-server/src/main/resources/application-local.yml:1)
- [application-prod.yml](/d:/project/msp/msp-server/src/main/resources/application-prod.yml:1)

### 4.3 로컬 DB 기본값

- DB 이름: `msp_overlay`
- 사용자: `msp_user`
- 비밀번호: `msp_password`

## 5. 인증 구조

### 5.1 로그인

- 시작 API: `GET /api/auth/google`
- 실제 Google OAuth 진입: `/oauth2/authorization/google`
- 성공 시 Frontend callback으로 redirect
- 발급 값:
  - access token
  - refresh token
  - token type
  - refresh token 만료 시각

### 5.2 인증 API

- `GET /api/auth/me`
- `POST /api/auth/refresh`
- `POST /api/auth/logout`

### 5.3 인증 방식

- API 보호는 JWT Bearer Token 기준
- Refresh Token은 DB 저장 방식
- OAuth 로그인 성공/실패 후 Frontend callback URL로 redirect

## 6. 조회 API

### 6.1 Platform

- `GET /api/platforms`

### 6.2 Game

- `GET /api/games?platform=windows`

### 6.3 Overlay

- `GET /api/overlays`
- `GET /api/overlays/{overlayId}`
- `GET /api/overlays/code/{code}`

지원 필터:

- `page`
- `size`
- `keyword`
- `platform`
- `game`
- `code`
- `sort`

## 7. Overlay 쓰기 API

### 7.1 업로드

- `POST /api/overlays`
- `multipart/form-data`

요청 필드:

- `name`
- `description`
- `code`
- `platform`
- `gameId`
- `overlayJson`
- `thumbnail`

### 7.2 수정

- `PATCH /api/overlays/{overlayId}`
- `multipart/form-data`

### 7.3 삭제

- `DELETE /api/overlays/{overlayId}`

## 8. overlay.json 규칙

- JSON Schema 검증 적용
- 루트 객체는 추가 속성 금지
- 서버가 허용하는 메타만 포함 가능
- `description`은 업로드 메타 필드이며 `overlay.json` 본문 필드가 아님

스키마 파일:

- [overlay-schema.json](/d:/project/msp/msp-server/src/main/resources/jsonschema/overlay-schema.json:1)

## 9. 썸네일 규칙

- thumbnail은 `.png`만 허용
- 저장 파일명은 고정 `thumbnail.png`
- overlay JSON 저장 파일명은 고정 `overlay.json`

저장 경로 예시:

```text
/storage/overlays/{overlayId}/overlay.json
/storage/overlays/{overlayId}/thumbnail.png
```

## 10. Library API

- `GET /api/library`
- `POST /api/library`
- `DELETE /api/library/{overlayId}`

동작:

- 로그인 사용자 기준 저장 목록 조회
- overlay 저장
- 저장 해제

## 11. DB 상태

### 11.1 기본 스키마

- users
- refresh_tokens
- platforms
- games
- overlays
- user_libraries

### 11.2 현재 시드 상태

- 플랫폼 기본값:
  - windows
  - android
- Windows 게임 시드 15종 추가됨

관련 마이그레이션:

- [V1__init_schema.sql](/d:/project/msp/msp-server/src/main/resources/db/migration/V1__init_schema.sql:1)
- [V2__seed_windows_games.sql](/d:/project/msp/msp-server/src/main/resources/db/migration/V2__seed_windows_games.sql:1)

## 12. Swagger

- local/dev: 활성화
- prod: 비활성화

로컬 확인 경로:

- `/v3/api-docs`
- `/swagger-ui/index.html`

## 13. 운영 전환 시 조정 항목

- `SPRING_PROFILES_ACTIVE=prod`
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `JWT_SECRET`
- `CORS_ALLOWED_ORIGINS`
- `FRONTEND_ORIGIN`
- `FRONTEND_LOGIN_CALLBACK_PATH`
- `APP_STORAGE_BASE_PATH`
- Google OAuth 운영 Client ID / Secret

운영 시 주의:

- 민감값은 `application-prod.yml`에 직접 하드코딩하지 않고 환경변수로 주입
- HTTPS 필수
- Google OAuth redirect URI 운영 도메인 반영
- 파일 저장 경로 쓰기 권한 확인

## 14. 현재 로컬 기준 결론

현재 서버는 로컬 1차 완료 기준에서 다음을 충족한다.

- 인증 API 동작 가능
- Overlay 조회/업로드/수정/삭제 가능
- Library 저장 흐름 가능
- JSON Schema 검증과 파일 저장 규칙 반영
- Swagger 문서 확인 가능
- prod 전환을 위한 설정 분리 구조 보유

## 15. 남은 운영 준비 항목

- 운영 환경 변수 확정
- 운영용 OAuth redirect URI 적용
- 운영 스토리지 경로 확정
- Docker 이미지/실행 스크립트 정리
- 실제 운영 도메인 기준 CORS 점검
- 필요 시 reverse proxy / forwarded header 설정 추가
