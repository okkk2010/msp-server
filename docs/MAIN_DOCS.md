 ````md
# msp overlay 전체 시스템 아키텍처 문서

## 1. 문서 목적

이 문서는 **msp overlay** 프로젝트의 전체 시스템 구조를 한 번에 이해할 수 있도록 정리한 아키텍처 문서다.

본 시스템은 단순한 로컬 오버레이 실행 프로그램이 아니라, 다음 기능을 하나의 흐름으로 통합하는 구조를 목표로 한다.

- 오버레이 제작
- 로컬 저장 및 불러오기
- 서버 업로드
- 웹 탐색 및 공유
- 사용자 라이브러리 저장
- Windows / Android 클라이언트 적용

상세제안서에서도 본 프로젝트를 **Windows와 Android 환경에서 동작하는 멀미 방지 오버레이 클라이언트**와, 오버레이를 **공유·저장할 수 있는 웹사이트와 서버를 함께 구성하는 시스템**으로 정의하고 있다. :contentReference[oaicite:0]{index=0}

---

## 2. 시스템 정의

**msp overlay**는 게임 멀미 완화를 위한 오버레이를 사용자가 직접 제작하고, 로컬에 저장하거나 서버에 업로드하며, 웹에서 탐색하고, 개인 라이브러리에 저장한 뒤, Windows 및 Android 클라이언트에서 다시 불러와 적용할 수 있는 통합 플랫폼이다. 

---

## 3. 시스템 구성 요소

## 3.1 Web Frontend

웹 프론트엔드는 오버레이를 탐색하고 관리하는 **커뮤니티 허브** 역할을 담당한다.

주요 역할:

- 오버레이 목록 조회
- 6자리 코드 검색
- 플랫폼별 필터
- 게임별 필터
- 오버레이 상세 조회
- 오버레이 업로드
- 내 라이브러리 저장 및 조회
- 로그인 상태 기반 기능 제어

상세제안서에서도 웹사이트 기능으로 오버레이 업로드, 오버레이 코드 및 설명 저장, 플랫폼·게임 기준 카테고리 분류, 다른 사용자가 만든 오버레이 라이브러리 저장, 무료 코드 열람 및 활용을 제시하고 있다. :contentReference[oaicite:2]{index=2}

기술 스택:

- React
- Vite
- Tailwind CSS v4
- VS Code
- Vercel 배포 예정 :contentReference[oaicite:3]{index=3}

---

## 3.2 Backend Server

백엔드는 전체 시스템의 **공용 데이터 허브** 역할을 담당한다.

주요 역할:

- 사용자 계정 관리
- Google OAuth 기반 사용자 식별
- 오버레이 메타데이터 저장
- 오버레이 JSON 원본 저장 또는 참조 관리
- 사용자 라이브러리 관리
- 플랫폼 / 게임 카테고리 관리
- 웹 / Windows / Android 공통 REST API 제공
- 파일 저장소와 DB 연동

상세제안서에서는 서버 기능으로 사용자 계정, 라이브러리, 카테고리 정보 저장, 클라이언트/웹 요청에 대한 REST API 제공, 오버레이 구성 파일과 메타데이터 연동, DB 및 스토리지를 통한 사용자별 데이터 관리를 제시한다. :contentReference[oaicite:4]{index=4}

기술 스택:

- Spring Boot
- RESTful API
- PostgreSQL
- IntelliJ
- Docker / Docker Compose
- Ubuntu Server 운영 예정 :contentReference[oaicite:5]{index=5}

---

## 3.3 Windows Client

Windows 클라이언트는 본 프로젝트의 **핵심 실행기이자 편집기**다.

주요 역할:

- 실행 중인 게임 또는 프로그램 창 추적
- 오버레이 편집
- 오버레이 렌더링
- 로컬 저장 / 불러오기
- 서버 업로드 및 라이브러리 연동
- 게임별 오버레이 지정
- 원격 제어 UI 또는 설정 UI 연동

상세제안서에서도 Windows 클라이언트는 선택한 프로그램을 트래킹하여 오버레이를 출력하고, 게임별 overlay 지정 및 로컬 파일 관리를 수행하는 구조로 정리되어 있다. :contentReference[oaicite:6]{index=6}

기술 스택:

- C# (.NET)
- Visual Studio :contentReference[oaicite:7]{index=7}

---

## 3.4 Android Client

Android 클라이언트는 모바일 환경 대응을 위한 실행기다.

주요 역할:

- 전체 화면 기준 오버레이 출력
- 알림창(Notification) 기반 on/off 제어
- 필요 시 라이브러리에서 오버레이 불러오기
- JSON 기반 오버레이 렌더링

상세제안서에서도 Android 클라이언트는 전체 화면 기준 overlay 출력과 알림창을 통한 overlay on/off 및 설정 제어를 담당한다. :contentReference[oaicite:8]{index=8}

기술 스택:

- Java
- Android Studio
- Gradle :contentReference[oaicite:9]{index=9}

---

## 3.5 Database / File Storage

데이터 저장 구조는 두 계층으로 나뉜다.

### Database
검색, 목록, 필터, 계정, 권한, 라이브러리 저장을 위한 구조화 데이터 저장소

### File Storage
실제 오버레이 원본 파일과 썸네일 저장소

오버레이 저장 구조 초안에 따르면 오버레이 1개는 기본적으로 아래 단위로 저장된다. :contentReference[oaicite:10]{index=10}

- `overlay.json`
- `thumbnail.png`

또한 JSON 내부의 `meta.createdAt`, `meta.updatedAt`와 별도로 DB에는 `created_at`, `updated_at`를 유지한다. 이 둘은 각각 **파일 자체의 생성/수정 이력**과 **서버 레코드의 생성/수정 이력**을 의미한다. :contentReference[oaicite:11]{index=11}

---

## 4. 오버레이 저장 구조

## 4.1 저장 단위

오버레이 하나는 다음 두 파일을 기본 단위로 가진다.

- `overlay.json`: 편집 가능한 오버레이 원본 데이터
- `thumbnail.png`: 미리보기 이미지

현재 구조에서는 `image` 요소를 지원하지 않기 때문에 `assets` 폴더는 포함하지 않는다. :contentReference[oaicite:12]{index=12}

---

## 4.2 JSON 최상위 구조

오버레이 JSON은 아래와 같은 구조를 가진다. :contentReference[oaicite:13]{index=13}

- `schemaVersion`
- `overlayId`
- `name`
- `platform`
- `game`
- `canvas`
- `overlaySettings`
- `elements`
- `meta`

예시 구조:

```json
{
  "schemaVersion": "1.0.0",
  "overlayId": "ovl_001",
  "name": "Minecraft Center Focus",
  "platform": "windows",
  "game": {
    "id": "minecraft",
    "name": "Minecraft"
  },
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
  }
}
````



---

## 4.3 canvas 구조

`canvas`는 편집 기준 해상도 정보만 저장한다. 

* `baseWidth`
* `baseHeight`

현재는 다음 항목은 저장하지 않는다.

* `scaleMode`
* `unit`
* `origin`

---

## 4.4 overlaySettings 구조

`overlaySettings`에는 현재 `opacity`만 저장한다. 

* `opacity`

현재 저장 파일에서 제외되는 실행 정책 항목:

* `visible`
* `clickThrough`
* `followTargetWindow`
* `showOnlyWhenTargetFocused`
* `alwaysOnTop`
* `anchorPreset`
* `offsetX`
* `offsetY`

이 값들은 저장 데이터가 아니라 **클라이언트 실행 정책**으로 관리한다. 

---

## 4.5 elements 구조

현재 지원하는 오버레이 요소 타입은 다음 세 가지다. 

* `rect`
* `circle`
* `line`

현재 제외된 요소 타입:

* `image`
* `text`

즉 MVP 기준 오버레이 렌더링은 **사각형, 원, 선**만 처리하면 된다. 

---

## 4.6 meta 구조

`meta`에는 최소 관리 정보만 포함한다. 

* `createdAt`
* `updatedAt`

현재 제외되는 값:

* 작성자
* 공개 범위
* 태그
* 버전 정보

---

## 5. 시스템 도메인 구조

## 5.1 User

Google 로그인 기반 사용자

주요 속성:

* id
* oauth provider
* provider user id
* email
* name
* profile image
* created_at
* updated_at

상세제안서에서도 Google 로그인 기반 사용자 인증 기능 제공을 요구하고 있다. 

---

## 5.2 Platform

오버레이 대상 플랫폼 분류

초기 고정값:

* `windows`
* `android`

JSON 명세에서도 `platform`은 문자열로 저장하며 예시 값으로 `windows`, `android`를 사용한다. 

---

## 5.3 Game

플랫폼별 게임 분류 정보

권장 속성:

* id
* platform_id
* slug
* display_name

JSON 명세에서는 `game.id`, `game.name`의 object 형태를 사용한다. 

---

## 5.4 Overlay

오버레이는 시스템 안에서 두 가지 층위로 관리된다.

### A. Overlay Metadata

검색과 분류를 위한 구조화 정보

권장 속성:

* id
* overlay_id
* code
* name
* description
* platform_id
* game_id
* author_user_id
* thumbnail_url
* schema_version
* created_at
* updated_at

### B. Overlay Content

실제 렌더링 가능한 JSON 원본 데이터

포함 정보:

* canvas
* overlaySettings
* elements
* meta

즉 오버레이는 단순 게시물이 아니라, **검색 가능한 메타데이터 + 편집 가능한 원본 문서**의 조합이다.

---

## 5.5 UserLibrary

사용자가 저장한 오버레이 목록

권장 속성:

* id
* user_id
* overlay_id
* created_at

상세제안서에서는 로그인한 사용자가 웹사이트 또는 클라이언트에서 자신의 라이브러리를 불러올 수 있고, 다른 사용자가 만든 오버레이를 라이브러리에 저장할 수 있는 구조를 요구한다. 

---

## 6. 전체 데이터 흐름

## 6.1 로컬 제작 흐름

1. 사용자가 Windows 클라이언트에서 오버레이를 제작한다.
2. 클라이언트는 편집 상태를 `overlay.json`으로 저장한다.
3. 썸네일을 생성해 `thumbnail.png`로 저장한다.

이 구조는 오버레이 저장 단위 명세와 동일하다. 

---

## 6.2 서버 업로드 흐름

1. 사용자가 로그인한다.
2. 웹 또는 Windows 클라이언트에서 업로드를 요청한다.
3. 서버는 JSON 구조를 검증한다.
4. 서버는 메타데이터를 추출한다.
5. DB에 메타데이터를 저장한다.
6. 파일 저장소에 JSON 원본과 썸네일을 저장한다.

즉 서버는 단순 파일 업로드 서버가 아니라, **오버레이 문서를 이해하고 검증하는 서버**가 된다.

---

## 6.3 웹 탐색 흐름

1. 사용자가 웹에서 플랫폼, 게임, 코드로 검색한다.
2. 서버는 DB에서 메타데이터를 조회한다.
3. 목록과 썸네일을 반환한다.
4. 상세 페이지에서는 필요 시 JSON 기반 정보까지 표시한다.

상세제안서의 웹사이트 업로드/조회/저장 기능과 사용자 라이브러리 및 카테고리 기반 오버레이 검색 기능 요구와 일치한다. 

---

## 6.4 라이브러리 저장 흐름

1. 사용자가 원하는 오버레이를 라이브러리에 저장한다.
2. `user_libraries`에 사용자-오버레이 매핑이 생성된다.
3. 동일 계정으로 웹, Windows, Android에서 다시 조회할 수 있다.

---

## 6.5 클라이언트 적용 흐름

1. Windows 또는 Android 클라이언트가 서버에서 오버레이를 가져온다.
2. JSON 원본을 파싱한다.
3. `canvas`, `overlaySettings.opacity`, `elements`를 기준으로 렌더링한다.
4. 플랫폼 특성에 맞게 실행한다.

Windows는 창 추적 기반, Android는 전체 화면 기준으로 동작한다. 

---

## 7. API 구조 초안

## 7.1 인증 API

### `GET /api/auth/me`

현재 로그인한 사용자 정보 조회

---

## 7.2 카테고리 API

### `GET /api/platforms`

플랫폼 목록 조회

### `GET /api/games`

게임 목록 조회

---

## 7.3 오버레이 API

### `GET /api/overlays`

오버레이 목록 조회

반환 핵심 정보:

* overlayId
* code
* name
* description
* platform
* game
* thumbnailUrl
* author
* isSaved

### `GET /api/overlays/{id}`

오버레이 상세 조회

반환 정보:

* 메타데이터
* 필요 시 JSON 원본 또는 JSON 다운로드 URL

### `POST /api/overlays`

오버레이 업로드

입력 예시:

* name
* description
* platform / game
* overlay.json
* thumbnail.png

1차 MVP에서는 `JSON text + metadata` 형태로도 처리 가능하다. 이 방향은 현재 JSON 원본 중심 구조와 부합한다.

---

## 7.4 라이브러리 API

### `GET /api/library`

내 라이브러리 조회

### `POST /api/library`

내 라이브러리 저장

---

## 8. DB 구조 초안

## 8.1 users

사용자 계정 정보

## 8.2 platforms

플랫폼 분류 정보

## 8.3 games

플랫폼 종속 게임 분류 정보

## 8.4 overlays

검색용 메타데이터 + JSON 참조 정보

권장 컬럼:

* id
* overlay_id
* code
* name
* description
* platform_id
* game_id
* author_user_id
* schema_version
* canvas_base_width
* canvas_base_height
* opacity
* json_content 또는 json_path
* thumbnail_url
* created_at
* updated_at

여기서 `canvas.baseWidth`, `canvas.baseHeight`, `overlaySettings.opacity`는 JSON 안에도 존재하지만, 목록 조회와 빠른 검색을 위해 일부 중복 저장할 수 있다. 이 값들은 JSON 명세상 최상위 주요 렌더링 정보다. 

## 8.5 user_libraries

사용자 라이브러리 매핑 정보

---

## 9. 클라이언트 모듈 구조

## 9.1 Windows Client 모듈

### Overlay Editor

* rect / circle / line 편집
* opacity 조절
* canvas 기준 해상도 관리
* 요소 선택 / 잠금 / 표시 여부 관리

### Overlay Serializer

* JSON 저장
* JSON 불러오기
* schemaVersion 호환 처리

### Overlay Renderer

* JSON 파싱
* rect / circle / line 렌더링
* 게임 창에 맞는 스케일 계산

### Sync Module

* 서버 업로드
* 라이브러리 다운로드
* 계정 연동

이 구조는 현재 JSON 명세와 직접 연결된다. 

---

## 9.2 Android Client 모듈

### Overlay Renderer

* JSON 파싱
* rect / circle / line 렌더링
* 전체 화면 기준 배치

### Notification Controller

* 알림창 기반 on/off 제어
* 설정 진입

### Sync Module

* 서버 연동
* 라이브러리 다운로드
* 계정 연동

Android는 Windows처럼 특정 창을 추적하기보다 전체 화면 기준으로 렌더링한다. 

---

## 10. 프론트엔드가 알아야 할 JSON 정보

웹 프론트는 실제 렌더링 엔진은 아니지만, JSON 구조를 이해해야 한다.

이유:

* 업로드 검증
* 상세 페이지 정보 표시
* 지원 요소 타입 안내
* 스키마 버전 표시
* 오버레이 구성 요약 표시

상세 페이지에서 표시 가능한 값 예시:

* base canvas size
* global opacity
* element count
* element types
* last updated

이 값들은 모두 JSON 원본에서 직접 추출할 수 있다. 

---

## 11. 구현 우선순위

현재 시스템 우선순위는 다음과 같이 잡는 것이 적절하다.

1. Backend Server
2. Frontend Web
3. Windows Client 연동
4. Android Client 연동

상세제안서 일정표에서도 4월 설계 후 5월에 웹, Windows, Android, API 구현을 진행하고, 이후 통합 점검과 시연 준비를 수행하는 구조로 제시되어 있다. 일정표는 3월 계획/분석, 4월 설계, 5월 시스템 구축, 6월 시범 운영·보완·문서화·최종보고 흐름을 보여준다. 

---

## 12. 현재 시스템의 핵심 원칙

* 오버레이는 단순 게시물이 아니라 **편집 가능한 원본 문서**다.
* 서버는 단순 CRUD 서버가 아니라 **JSON 문서 검증 및 메타데이터 관리 허브**다.
* 웹은 단순 소개 페이지가 아니라 **탐색, 저장, 업로드를 담당하는 커뮤니티 허브**다.
* Windows와 Android는 같은 오버레이 JSON을 공유하되, **실행 방식은 플랫폼별로 다르게 처리**한다.
* MVP 단계에서는 구조를 단순하게 유지하기 위해 요소 타입을 `rect`, `circle`, `line`으로 제한한다. 

---

## 13. 최종 정리

현재 기준에서 **msp overlay 전체 시스템**은 아래와 같이 정리할 수 있다.

* 사용자는 Windows 클라이언트에서 오버레이를 제작한다.
* 오버레이는 `overlay.json`과 `thumbnail.png` 형태로 로컬 저장된다.
* 서버는 이 오버레이를 업로드받아 JSON을 검증하고, DB에는 메타데이터를 저장하며, 파일 저장소에는 원본 JSON과 썸네일을 저장한다.
* 웹 프론트는 저장된 오버레이를 코드, 플랫폼, 게임 기준으로 탐색하고, 상세 정보를 보여주며, 라이브러리에 저장할 수 있게 한다.
* Windows 및 Android 클라이언트는 동일한 JSON 원본을 불러와 각 플랫폼 실행 방식에 맞게 렌더링한다.

즉, **msp overlay는 로컬 편집기, 서버, 웹 허브, 멀티플랫폼 실행기를 하나로 연결하는 오버레이 통합 플랫폼**이다.

```

다음 단계로는 이 문서를 기준으로  
**backend 명세서 수정본**이나 **DB 테이블 명세서**를 바로 이어서 만들면 됩니다.
```
