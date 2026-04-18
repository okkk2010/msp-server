# msp overlay Backend Specification

## 1. 문서 목적

이 문서는 msp overlay 프로젝트의 백엔드 서버 구현 기준을 정의한다. 백엔드는 사용자 인증, 오버레이 메타데이터 관리, overlay.json 검증/저장, 썸네일 저장, 플랫폼/게임 카테고리, 사용자 라이브러리, Web/Windows/Android 공통 API를 담당한다.


## 확정 결정 사항

| 항목 | 확정 내용 |
|---|---|
| 인증 방식 | JWT 기반 |
| Google OAuth | 사용 |
| Access Token 만료 | 30분 |
| Refresh Token 만료 | 14일 |
| Refresh Token 저장 | DB 저장 |
| JWT 라이브러리 | JJWT |
| JSON Schema Validator | networknt json-schema-validator |
| 업로드 방식 | multipart/form-data |
| JSON 저장 | 파일 저장 후 DB에는 json_path 저장 |
| 시간 타입 | PostgreSQL timestamptz |
| 게임 정보 | 등록 시 필수 아님, 이후 수정 가능 |
| 오버레이 수정/삭제 | MVP 포함 |
| 라이브러리 삭제 | MVP 포함 |
| 삭제 방식 | hard delete |
| 삭제 시 파일 처리 | overlay.json, thumbnail.png 즉시 삭제 |
| Docker Compose | 초기 개발환경 필수 |
| Swagger | 사용, 운영 환경에서는 비공개 |
| Flyway | 사용 |
| Testcontainers | 사용 |
| 운영 HTTPS | 필수 |


## 2. 백엔드 역할

```text
Web Frontend / Windows Client / Android Client
        ↓
Backend Server
        ↓
PostgreSQL + File Storage
```

Backend는 다음 책임을 가진다.

- Google OAuth 로그인 처리
- JWT Access Token / Refresh Token 발급
- Refresh Token DB 저장 및 로그아웃 시 삭제
- 오버레이 목록/상세 조회
- multipart/form-data 기반 오버레이 업로드
- overlay.json JSON Schema 검증
- overlay.json 및 thumbnail.png 파일 저장
- 오버레이 수정/삭제
- 오버레이 삭제 시 DB hard delete 및 파일 즉시 삭제
- 사용자 라이브러리 저장/조회/삭제
- 공통 응답/예외 처리

## 3. 기술 스택

- Java
- Spring Boot
- Spring Web
- Spring Security
- OAuth2 Client
- Spring Data JPA
- PostgreSQL
- Flyway
- JJWT
- networknt json-schema-validator
- springdoc-openapi
- Testcontainers
- Docker / Docker Compose

## 4. MVP 범위

### 포함

- Google OAuth Login
- JWT 인증
- Access Token 30분, Refresh Token 14일
- Refresh Token DB 저장
- Platform / Game 조회
- Overlay 목록/상세 조회
- Overlay 업로드/수정/삭제
- UserLibrary 저장/조회/삭제
- Swagger 개발 문서
- Flyway 마이그레이션
- Testcontainers 통합 테스트

### 제외

- 좋아요/댓글/신고
- 관리자 검수
- 추천 알고리즘
- 다운로드 통계
- 오버레이 버전 히스토리
- Refresh Token 해시 저장
- 외부 오브젝트 스토리지 연동

## 5. 패키지 구조

```text
com.mspoverlay
 ┣ global
 ┃ ┣ config
 ┃ ┣ exception
 ┃ ┣ response
 ┃ ┣ security
 ┃ ┗ util
 ┣ domain
 ┃ ┣ auth
 ┃ ┣ user
 ┃ ┣ platform
 ┃ ┣ game
 ┃ ┣ overlay
 ┃ ┗ library
 ┣ infrastructure
 ┃ ┣ oauth
 ┃ ┣ storage
 ┃ ┗ jsonschema
 ┗ MspOverlayApplication.java
```

## 6. 구현 순서

1. Spring Boot 프로젝트 생성
2. Docker Compose PostgreSQL 구성
3. Flyway DB 마이그레이션 구성
4. 공통 응답/예외 구조 구성
5. User / RefreshToken / Platform / Game / Overlay / UserLibrary 도메인 구성
6. Google OAuth + JWT 구성
7. Overlay JSON Schema 검증 구성
8. multipart/form-data 업로드 구성
9. Overlay 조회/수정/삭제 API 구성
10. Library 저장/조회/삭제 API 구성
11. Swagger 문서화
12. Testcontainers 통합 테스트 구성

## 7. 완료 기준

- Docker Compose로 PostgreSQL 실행 가능
- Flyway migration 적용 가능
- Google OAuth 로그인 가능
- JWT 발급/검증 가능
- Refresh Token DB 저장/삭제 가능
- multipart/form-data로 overlayJson, thumbnail 업로드 가능
- overlay.json Schema 검증 가능
- json_path, thumbnail_path DB 저장 가능
- 오버레이 hard delete 가능
- 삭제 시 overlay.json, thumbnail.png 즉시 삭제 가능
- 라이브러리 저장/조회/삭제 가능
- 운영 환경에서 Swagger 비활성화 가능
