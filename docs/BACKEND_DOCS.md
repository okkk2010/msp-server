````md
# msp overlay Backend Specification

## 1. 문서 목적

이 문서는 **msp overlay** 프로젝트의 백엔드 서버를 구현하기 위한 상세 명세서다.

이 백엔드는 단순 게시물 CRUD 서버가 아니라, 다음 역할을 함께 수행하는 **공통 데이터 허브**로 설계한다.

- 사용자 계정 식별 및 인증 연동
- 오버레이 메타데이터 관리
- 오버레이 JSON 원본 저장 및 검증
- 플랫폼 / 게임 카테고리 관리
- 사용자 라이브러리 관리
- Web / Windows / Android 클라이언트 공통 API 제공

상세제안서에서도 서버는 사용자 계정, 라이브러리, 카테고리 정보 저장, REST API 제공, 오버레이 구성 파일과 메타데이터 연동, DB 및 스토리지를 통한 사용자별 데이터 관리를 담당하는 구조로 정의되어 있다. :contentReference[oaicite:0]{index=0}

---

## 2. 백엔드의 시스템 내 역할

msp overlay 전체 구조에서 백엔드는 다음 위치에 있다.

- **Web Frontend**는 탐색, 검색, 업로드, 라이브러리 UI를 담당
- **Backend Server**는 인증, 데이터 저장, 검색, 업로드 처리, 라이브러리 관리 담당
- **Windows Client**는 오버레이 제작, 로컬 저장, 업로드, 렌더링 담당
- **Android Client**는 전체 화면 기준 오버레이 렌더링 담당

백엔드는 이 모든 클라이언트가 공통으로 참조하는 중심축이다. 상세제안서에서도 Windows와 Android 클라이언트, 웹사이트, 서버를 함께 구성하는 통합 시스템으로 설명한다. :contentReference[oaicite:1]{index=1}

---

## 3. 현재 구현 우선순위

현재 우선순위는 다음과 같다.

1. Backend Server
2. Frontend Web
3. Windows Client 연동
4. Android Client 연동

즉, 지금 단계의 백엔드는 **웹 우선 MVP**를 지원하되, 이후 Windows / Android 연동까지 고려한 구조여야 한다.

---

## 4. 기술 스택

### 기본 기술
- Java
- Spring Boot
- Spring Data JPA
- PostgreSQL
- RESTful API
- Docker
- Docker Compose
- Ubuntu Server

### 개발 도구
- IntelliJ IDEA
- Postman
- GitHub
- GitHub Desktop

상세제안서의 구현 기술 고찰 및 개발 툴 항목도 서버에 Spring Boot, RESTful API, PostgreSQL, IntelliJ를 사용한다고 명시한다. :contentReference[oaicite:2]{index=2}

---

## 5. 백엔드 핵심 책임

## 5.1 인증 및 사용자 식별
- Google OAuth 기반 로그인 사용자 식별
- 사용자 최초 로그인 시 계정 생성
- 기존 로그인 사용자는 계정 조회
- 로그인 기반 권한 체크

상세제안서에서도 Google 로그인 기반 사용자 인증 기능 제공을 요구한다. :contentReference[oaicite:3]{index=3}

## 5.2 오버레이 메타데이터 관리
- 오버레이 이름
- 6자리 코드
- 설명
- 플랫폼
- 게임
- 작성자
- 썸네일
- 생성/수정 시각
- JSON 원본 참조 정보 저장

## 5.3 오버레이 JSON 검증 및 저장
- 업로드된 `overlay.json` 구조 검증
- JSON에서 메타데이터 추출
- JSON 원본 저장
- 썸네일 파일 저장
- JSON과 DB 메타데이터 연동

오버레이 저장 구조 초안에 따르면 오버레이는 `overlay.json`과 `thumbnail.png` 단위로 저장하며, JSON에는 `schemaVersion`, `overlayId`, `name`, `platform`, `game`, `canvas`, `overlaySettings.opacity`, `elements`, `meta`가 포함된다. :contentReference[oaicite:4]{index=4}

## 5.4 라이브러리 관리
- 사용자별 저장 오버레이 관리
- 중복 저장 방지
- 라이브러리 목록 조회

상세제안서에서도 로그인한 사용자가 웹사이트 또는 클라이언트에서 자신의 라이브러리를 불러올 수 있고, 다른 사용자가 만든 오버레이를 라이브러리에 저장할 수 있는 구조를 요구한다. :contentReference[oaicite:5]{index=5}

## 5.5 카테고리 관리
- 플랫폼 목록 조회
- 플랫폼별 게임 목록 조회
- 검색 / 필터용 기준 데이터 제공

---

## 6. 이번 단계 구현 범위

## 6.1 반드시 구현할 범위
- Spring Boot 프로젝트 초기 세팅
- PostgreSQL 연동
- Docker / Docker Compose 구성
- Google OAuth 연동 또는 그에 준하는 사용자 식별 구조
- 사용자 정보 조회 API
- 플랫폼 목록 조회 API
- 게임 목록 조회 API
- 오버레이 목록 조회 API
- 오버레이 상세 조회 API
- 오버레이 업로드 API
- 내 라이브러리 저장 API
- 내 라이브러리 조회 API
- 공통 예외 처리
- 공통 응답 포맷
- JSON 스키마 검증 로직
- 시드 데이터 구성

## 6.2 보류 가능한 범위
- 관리자 기능
- 신고 / 검수 / 승인 시스템
- 좋아요 / 댓글 / 다운로드 수
- 추천 시스템
- 통계 시스템
- 버전 히스토리 관리
- 공개범위 세분화
- 대용량 파일 저장소 분리

---

## 7. 설계 원칙

## 7.1 구조 원칙
- API는 Web / Windows / Android가 공통으로 재사용 가능해야 한다.
- Entity를 그대로 외부에 노출하지 않는다.
- DTO를 명확히 분리한다.
- 오버레이는 게시물이 아니라 **메타데이터 + JSON 원본 문서**로 다룬다.
- 검색용 데이터와 원본 데이터를 분리한다.
- MVP는 단순하게, 확장은 가능한 방향으로 설계한다.

## 7.2 JSON 처리 원칙
- JSON 전체를 원본 그대로 보관한다.
- 동시에 일부 주요 필드는 DB에 구조화해 저장한다.
- JSON 원본이 기준 데이터다.
- DB 메타데이터는 검색/목록/필터/빠른 조회를 위한 보조 구조다.

이 방향은 오버레이 저장 구조 초안의 “JSON 원문 또는 구조화 데이터 저장”, “DB created_at / updated_at 별도 유지” 원칙과 일치한다. :contentReference[oaicite:6]{index=6}

---

## 8. 오버레이 JSON 기준 스펙

백엔드는 아래 JSON 구조를 이해하고 검증해야 한다.

## 8.1 최상위 필드
- `schemaVersion`
- `overlayId`
- `name`
- `platform`
- `game`
- `canvas`
- `overlaySettings`
- `elements`
- `meta`

:contentReference[oaicite:7]{index=7}

## 8.2 canvas
- `baseWidth`
- `baseHeight`

## 8.3 overlaySettings
- `opacity`

## 8.4 elements
현재 허용 타입:
- `rect`
- `circle`
- `line`

현재 제외 타입:
- `image`
- `text`

## 8.5 meta
- `createdAt`
- `updatedAt`

즉, 백엔드는 최소한 위 구조가 유효한지 검사해야 하며, 특히 `platform`, `canvas`, `overlaySettings.opacity`, `elements`, `meta`가 정상 형식인지 확인해야 한다. :contentReference[oaicite:8]{index=8}

---

## 9. 도메인 모델

## 9.1 User
Google 로그인 기반 사용자

### 필드
- id
- oauthProvider
- oauthProviderUserId
- email
- name
- profileImageUrl
- createdAt
- updatedAt

### 제약
- `(oauthProvider, oauthProviderUserId)` unique

---

## 9.2 Platform
오버레이 대상 플랫폼

### 필드
- id
- name
- slug
- isActive
- createdAt
- updatedAt

### 초기값
- Windows / `windows`
- Android / `android`

JSON 명세에서도 `platform` 예시 값은 `windows`, `android`다. :contentReference[oaicite:9]{index=9}

---

## 9.3 Game
플랫폼별 게임 카테고리

### 필드
- id
- platformId
- slug
- displayName
- isActive
- createdAt
- updatedAt

### 예시
- minecraft
- valorant
- overwatch
- pubg-mobile

JSON에서는 `game.id`, `game.name` object 구조를 사용한다. :contentReference[oaicite:10]{index=10}

---

## 9.4 Overlay
오버레이 메타데이터 + 원본 JSON 참조 정보

### 필드
- id
- overlayId
- code
- name
- description
- platformId
- gameId
- authorUserId
- schemaVersion
- canvasBaseWidth
- canvasBaseHeight
- opacity
- jsonContent 또는 jsonPath
- thumbnailUrl
- createdAt
- updatedAt

### 설명
- `overlayId`: JSON 내부 식별자
- `code`: 웹 검색용 6자리 코드
- `jsonContent/jsonPath`: 원본 JSON 저장 위치
- `canvasBaseWidth`, `canvasBaseHeight`, `opacity`는 빠른 조회용 중복 저장 가능

오버레이 저장 구조 초안에서도 JSON과 별개로 DB에 서버 레코드 생성/수정 시각을 유지하고, JSON 안의 주요 정보와 DB 저장을 함께 고려하는 구조로 정리되어 있다. :contentReference[oaicite:11]{index=11}

---

## 9.5 UserLibrary
사용자 라이브러리 매핑 정보

### 필드
- id
- userId
- overlayId
- createdAt

### 제약
- `(userId, overlayId)` unique

---

## 10. DB 구조 초안

## 10.1 users
```sql
create table users (
    id bigserial primary key,
    oauth_provider varchar(30) not null,
    oauth_provider_user_id varchar(255) not null,
    email varchar(255),
    name varchar(100) not null,
    profile_image_url text,
    created_at timestamp not null,
    updated_at timestamp not null,
    unique (oauth_provider, oauth_provider_user_id)
);
````

## 10.2 platforms

```sql
create table platforms (
    id bigserial primary key,
    name varchar(50) not null unique,
    slug varchar(50) not null unique,
    is_active boolean not null default true,
    created_at timestamp not null,
    updated_at timestamp not null
);
```

## 10.3 games

```sql
create table games (
    id bigserial primary key,
    platform_id bigint not null references platforms(id),
    slug varchar(100) not null,
    display_name varchar(100) not null,
    is_active boolean not null default true,
    created_at timestamp not null,
    updated_at timestamp not null,
    unique (platform_id, slug)
);
```

## 10.4 overlays

```sql
create table overlays (
    id bigserial primary key,
    overlay_id varchar(100) not null unique,
    code varchar(6) not null unique,
    name varchar(150) not null,
    description text,
    platform_id bigint not null references platforms(id),
    game_id bigint references games(id),
    author_user_id bigint not null references users(id),
    schema_version varchar(20) not null,
    canvas_base_width int not null,
    canvas_base_height int not null,
    opacity numeric(3,2) not null,
    json_content text,
    json_path text,
    thumbnail_url text,
    created_at timestamp not null,
    updated_at timestamp not null
);
```

## 10.5 user_libraries

```sql
create table user_libraries (
    id bigserial primary key,
    user_id bigint not null references users(id),
    overlay_id bigint not null references overlays(id),
    created_at timestamp not null,
    unique (user_id, overlay_id)
);
```

---

## 11. 저장 전략

## 11.1 DB와 파일 저장소 역할 분리

* DB: 검색용 메타데이터 저장
* 파일 저장소: JSON 원본과 썸네일 저장

## 11.2 JSON 저장 방식 선택지

### A안. DB에 JSON 원문 저장

* 장점: 단순함, MVP 빠름
* 단점: 파일 관리 확장성 낮음

### B안. 파일 저장소에 JSON 저장 + DB에는 경로만 저장

* 장점: 실제 운영 구조와 가까움
* 단점: 초기 구현 복잡도 증가

## 11.3 권장안

MVP는 아래 둘 중 하나를 권장한다.

1. `json_content`에 원문 저장
2. 동시에 추후 전환 가능하도록 `json_path` 필드도 마련

즉, 초기엔 단순하게 가되 확장을 막지 않는다.

---

## 12. JSON 검증 규칙

## 12.1 필수 검증 항목

* `schemaVersion` 존재 여부
* `overlayId` 존재 여부
* `name` 존재 여부
* `platform` 존재 여부
* `canvas.baseWidth`, `canvas.baseHeight` 존재 여부
* `overlaySettings.opacity` 존재 여부
* `elements` 배열 여부
* `meta.createdAt`, `meta.updatedAt` 존재 여부

## 12.2 값 검증

* `platform`은 허용된 플랫폼만 가능
* `opacity`는 `0.0 ~ 1.0`
* `elements[].type`은 `rect`, `circle`, `line`만 허용
* `schemaVersion`은 지원 버전만 허용
* `code`는 6자리 규칙 만족

JSON 저장 구조 초안에서 현재 요소 타입과 `opacity`, `canvas`, `meta` 구조가 명확히 제한되어 있으므로, 서버도 동일 정책을 따라야 한다. 

## 12.3 code 규칙

권장 정규식:

```text
^[A-Z0-9]{6}$
```

---

## 13. 인증 정책

## 13.1 최소 정책

* 비로그인 사용자:

  * 오버레이 목록 조회 가능
  * 오버레이 상세 조회 가능
  * 플랫폼 / 게임 조회 가능

* 로그인 사용자:

  * 내 정보 조회 가능
  * 오버레이 업로드 가능
  * 라이브러리 저장 가능
  * 내 라이브러리 조회 가능

## 13.2 권장 구현 방향

현재 웹 우선 MVP이므로 세션 기반 또는 간단한 토큰 기반 둘 다 가능하다.
다만 이후 Windows / Android까지 고려하면, 장기적으로는 토큰 기반 확장이 더 유리하다.

---

## 14. API 명세

## 14.1 Auth API

### `GET /api/auth/me`

현재 로그인 사용자 정보 조회

#### 응답 예시

```json
{
  "success": true,
  "data": {
    "id": 1,
    "name": "Kim Jongmin",
    "email": "user@example.com",
    "profileImageUrl": "..."
  },
  "message": "ok"
}
```

---

## 14.2 Platform API

### `GET /api/platforms`

플랫폼 목록 조회

#### 응답 예시

```json
{
  "success": true,
  "data": [
    { "id": 1, "name": "Windows", "slug": "windows" },
    { "id": 2, "name": "Android", "slug": "android" }
  ],
  "message": "ok"
}
```

---

## 14.3 Game API

### `GET /api/games?platform=windows`

플랫폼별 게임 목록 조회

#### 응답 예시

```json
{
  "success": true,
  "data": [
    { "id": 10, "slug": "minecraft", "displayName": "Minecraft", "platformId": 1 }
  ],
  "message": "ok"
}
```

---

## 14.4 Overlay 목록 조회

### `GET /api/overlays`

목록 조회, 필터, 검색

#### 쿼리 파라미터

* `page`
* `size`
* `code`
* `keyword`
* `platform`
* `game`

#### 응답 필드

* id
* overlayId
* code
* name
* description
* platform
* game
* thumbnailUrl
* author
* isSaved
* createdAt
* updatedAt

#### 응답 예시

```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "overlayId": "ovl_001",
        "code": "A1B2C3",
        "name": "Minecraft Center Focus",
        "description": "중앙 시야 고정용 오버레이",
        "platform": { "id": 1, "name": "Windows", "slug": "windows" },
        "game": { "id": 10, "slug": "minecraft", "displayName": "Minecraft" },
        "thumbnailUrl": "...",
        "author": { "id": 1, "name": "Kim" },
        "isSaved": false,
        "createdAt": "2026-04-15T10:00:00",
        "updatedAt": "2026-04-15T10:00:00"
      }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 1,
    "totalPages": 1
  },
  "message": "ok"
}
```

---

## 14.5 Overlay 상세 조회

### `GET /api/overlays/{id}`

#### 반환 정보

* 메타데이터
* JSON 원본 또는 JSON 다운로드 참조값
* 라이브러리 저장 여부

#### 응답 예시

```json
{
  "success": true,
  "data": {
    "id": 1,
    "overlayId": "ovl_001",
    "code": "A1B2C3",
    "name": "Minecraft Center Focus",
    "description": "중앙 시야 고정용 오버레이",
    "platform": { "id": 1, "name": "Windows", "slug": "windows" },
    "game": { "id": 10, "slug": "minecraft", "displayName": "Minecraft" },
    "thumbnailUrl": "...",
    "schemaVersion": "1.0.0",
    "canvas": {
      "baseWidth": 1920,
      "baseHeight": 1080
    },
    "overlaySettings": {
      "opacity": 0.85
    },
    "elements": [],
    "meta": {
      "createdAt": "2026-04-15T19:30:00+09:00",
      "updatedAt": "2026-04-15T19:45:00+09:00"
    },
    "isSaved": true
  },
  "message": "ok"
}
```

---

## 14.6 Overlay 업로드

### `POST /api/overlays`

#### 권한

로그인 필요

#### 입력 방식

### 방식 A. multipart/form-data

* `name`
* `description`
* `platform`
* `gameId` 또는 `game`
* `code`
* `overlayJson`
* `thumbnail`

### 방식 B. application/json

* 메타데이터
* JSON text
* 썸네일 URL 또는 생략

#### 권장

MVP는 구현 단순성을 위해 `application/json` 또는 `multipart` 중 하나로 통일한다.
실제로는 클라이언트가 `overlay.json`을 다루므로 `multipart`가 더 자연스럽다.

#### 서버 처리 순서

1. 로그인 사용자 확인
2. code 중복 검사
3. 플랫폼 / 게임 유효성 검사
4. JSON 구조 검증
5. 메타데이터 추출
6. JSON 저장
7. 썸네일 저장
8. DB 저장

#### 응답 예시

```json
{
  "success": true,
  "data": {
    "id": 25,
    "overlayId": "ovl_025",
    "code": "M4N8Q2"
  },
  "message": "overlay created"
}
```

---

## 14.7 Library 저장

### `POST /api/library`

#### 요청 예시

```json
{
  "overlayId": 25
}
```

#### 처리

* 로그인 사용자 확인
* 대상 overlay 존재 확인
* 중복 저장 여부 확인
* 저장 처리

#### 응답 예시

```json
{
  "success": true,
  "data": null,
  "message": "saved"
}
```

---

## 14.8 Library 조회

### `GET /api/library`

#### 응답 필드

* libraryId
* savedAt
* overlay 요약 정보

---

## 15. DTO 명세

## 15.1 응답 DTO

* `UserMeResponse`
* `PlatformResponse`
* `GameResponse`
* `OverlaySummaryResponse`
* `OverlayDetailResponse`
* `LibraryItemResponse`

## 15.2 요청 DTO

* `OverlayCreateRequest`
* `LibrarySaveRequest`

---

## 16. 예외 처리

## 16.1 HTTP 상태 코드

* `400` Bad Request
* `401` Unauthorized
* `403` Forbidden
* `404` Not Found
* `409` Conflict
* `500` Internal Server Error

## 16.2 예외 코드 예시

* `INVALID_INPUT`
* `UNAUTHORIZED`
* `USER_NOT_FOUND`
* `PLATFORM_NOT_FOUND`
* `GAME_NOT_FOUND`
* `OVERLAY_NOT_FOUND`
* `OVERLAY_CODE_DUPLICATED`
* `INVALID_OVERLAY_JSON`
* `LIBRARY_ALREADY_SAVED`

#### 에러 응답 예시

```json
{
  "success": false,
  "code": "INVALID_OVERLAY_JSON",
  "message": "overlay json schema is invalid"
}
```

---

## 17. 패키지 구조 권장안

```text
com.mspoverlay
 ┣ global
 ┃ ┣ config
 ┃ ┣ exception
 ┃ ┣ response
 ┃ ┣ security
 ┃ ┗ util
 ┣ domain
 ┃ ┣ user
 ┃ ┣ platform
 ┃ ┣ game
 ┃ ┣ overlay
 ┃ ┗ library
 ┣ infrastructure
 ┃ ┣ storage
 ┃ ┗ oauth
 ┗ MspOverlayApplication.java
```

---

## 18. 구현 순서

## 1단계. 프로젝트 초기 세팅

* Spring Boot 생성
* 의존성 구성
* PostgreSQL 연결
* Docker Compose 구성
* 기본 패키지 구조 생성

## 2단계. 도메인 / DB 구축

* users
* platforms
* games
* overlays
* user_libraries

## 3단계. 시드 데이터

* platforms
* games 초기값 구성

## 4단계. 조회 API

* platforms 조회
* games 조회
* overlays 목록 조회
* overlays 상세 조회

## 5단계. 인증 연동

* 사용자 식별
* `/api/auth/me`

## 6단계. 쓰기 API

* overlays 업로드
* library 저장
* library 조회

## 7단계. 안정화

* 예외 처리
* 공통 응답 포맷
* Swagger 문서화
* 통합 테스트

---

## 19. 완료 기준

다음 항목이 충족되면 백엔드 1차 구현 완료로 본다.

* PostgreSQL과 정상 연결된다.
* 플랫폼/게임 시드 데이터가 준비된다.
* 비로그인 상태에서 목록/상세 조회가 가능하다.
* 로그인 사용자 조회가 가능하다.
* 로그인 후 오버레이 업로드가 가능하다.
* 업로드 시 JSON 구조 검증이 수행된다.
* 로그인 후 라이브러리 저장이 가능하다.
* 내 라이브러리 조회가 가능하다.
* Docker Compose로 서버와 DB를 함께 실행할 수 있다.
* 공통 에러 응답 구조가 적용되어 있다.

---

## 20. 최종 정리

msp overlay 백엔드는 다음 관점으로 이해해야 한다.

* 이 서버는 단순 게시판 서버가 아니다.
* 이 서버는 **오버레이 JSON 원본을 이해하고 관리하는 서버**다.
* Web, Windows, Android가 함께 참조할 공통 계정 / 오버레이 / 라이브러리 시스템이다.
* 현재는 웹 우선 MVP를 먼저 구현하지만, 이후 클라이언트 확장까지 고려해 구조를 세워야 한다.

즉, 지금 해야 할 일은 기능을 많이 붙이는 것이 아니라,
**검색 가능한 메타데이터 구조 + 검증 가능한 JSON 원본 저장 구조 + 재사용 가능한 API 구조**를 정확히 세우는 것이다.

```

이 버전 다음엔 바로  
**frontend 명세도 이 백엔드 구조에 맞춰 다시 정리**하면 연결이 깔끔해집니다.
```
