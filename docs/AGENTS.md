아래는 지금까지 확정한 내용을 기준으로 정리한 **msp overlay 전체 시스템 명세서 최신본**입니다.
기준 자료는 기존 상세제안서의 프로젝트 방향, 오버레이 JSON 저장 구조, 백엔드 명세, 전체 시스템 명세를 통합한 버전입니다.    

---

# msp overlay 전체 시스템 명세서

## 1. 문서 목적

이 문서는 **msp overlay 프로젝트의 전체 시스템 구조를 정의하는 상위 명세서**이다.

세부 API, DB 테이블, JSON Schema, Windows 클라이언트, Android 클라이언트, Frontend 구현 내용은 각각의 하위 명세서에서 관리한다.

이 문서의 목적은 다음과 같다.

* 전체 시스템의 구조 정리
* 각 구성 요소의 역할 정의
* 데이터 흐름 정리
* MVP 범위 확정
* 개발자가 어떤 세부 명세서를 참고해야 하는지 안내

---

## 2. 프로젝트 개요

**msp overlay**는 3D 그래픽 기반 1인칭·3인칭 게임 플레이 시 발생하는 멀미를 완화하기 위한 오버레이 시스템이다.

게임 화면 위에 평면형 그래픽 오버레이를 출력하여 사용자의 시각적 기준점을 만들고, 화면 전환이나 시점 이동에서 발생하는 입체감·괴리감을 줄이는 것을 목표로 한다.

기존 게임 내 HUD, 조준점, 범용 오버레이 도구는 다음 한계가 있다.

* 게임마다 지원 범위가 다름
* 멀미 완화 목적성이 약함
* 사용자별 오버레이 저장/공유 구조가 부족함
* 플랫폼/게임별 분류가 미흡함
* Android 환경에서는 Windows와 다른 오버레이 제어 방식이 필요함

따라서 msp overlay는 단순 로컬 프로그램이 아니라, 다음 기능을 하나의 시스템으로 연결한다.

* 오버레이 제작
* 로컬 저장 및 불러오기
* 서버 업로드
* 웹 기반 탐색 및 공유
* 사용자 라이브러리 저장
* Windows / Android 클라이언트 적용

---

## 3. 전체 시스템 한 줄 정의

**msp overlay는 오버레이 JSON 원본을 중심으로 Web, Backend, Windows Client, Android Client가 연결되는 멀티플랫폼 오버레이 제작·공유·실행 시스템이다.**

---

## 4. 전체 시스템 구성

```text
msp overlay
 ┣ Web Frontend
 ┣ Backend Server
 ┣ Windows Client
 ┣ Android Client
 ┣ Database
 ┣ File Storage
 ┗ Overlay JSON Format
```

각 영역은 독립적으로 개발되지만, 공통 기준은 다음 두 가지다.

1. **Overlay JSON Format**
2. **Backend REST API**

---

# 5. 시스템 구성 요소별 역할

## 5.1 Web Frontend

Web Frontend는 사용자가 오버레이를 탐색하고 관리하는 웹 허브다.

### 주요 역할

* 오버레이 목록 조회
* 6자리 코드 검색
* 플랫폼 / 게임 기준 필터링
* 오버레이 상세 조회
* 오버레이 업로드
* 내 라이브러리 저장
* 내 라이브러리 조회
* Google 로그인 진입
* 로그인 상태 기반 UI 제어

### 기술 스택

```text
React
Vite
Tailwind CSS v4
Axios
React Router
```

### 주요 화면

```text
/
 ┣ 홈 / 오버레이 목록
 ┣ 오버레이 상세
 ┣ 오버레이 업로드
 ┣ 내 라이브러리
 ┣ 로그인
 ┗ 마이페이지 또는 설정
```

### 참고 문서

```text
msp_overlay_frontend_spec.md
msp_overlay_ui_reference.md
msp_overlay_api_contract.md
```

---

## 5.2 Backend Server

Backend Server는 전체 시스템의 중심 데이터 허브다.

### 주요 역할

* 사용자 인증 및 계정 관리
* Google OAuth 기반 로그인
* JWT 기반 인증 처리
* 오버레이 메타데이터 관리
* 오버레이 JSON 원본 저장 및 검증
* 플랫폼 / 게임 카테고리 관리
* 사용자 라이브러리 관리
* Web / Windows / Android 공통 REST API 제공

### 기술 스택

```text
Java
Spring Boot
Spring Data JPA
Spring Security
JWT
PostgreSQL
Docker
Docker Compose
```

### 개발 도구

```text
IntelliJ
Postman
DBeaver
GitHub
GitHub Desktop
```

### 참고 문서

```text
msp_overlay_backend_spec.md
msp_overlay_api_contract.md
msp_overlay_db_spec.md
msp_overlay_json_schema_spec.md
msp_overlay_storage_spec.md
```

---

## 5.3 Windows Client

Windows Client는 오버레이 제작과 실제 실행을 담당하는 핵심 클라이언트다.

### 주요 역할

* 실행 중인 게임 또는 프로그램 창 추적
* 창 위치와 크기에 맞춰 오버레이 출력
* 오버레이 편집
* 로컬 `overlay.json` 저장
* 로컬 `overlay.json` 불러오기
* 썸네일 생성
* 서버 업로드
* 내 라이브러리 오버레이 다운로드
* JSON 기반 오버레이 렌더링
* Remote Control UI 제공
* 설정창 제공
* 핫키 설정 제공

### 기술 스택

```text
C#
.NET
Windows Forms 또는 WPF
Visual Studio
```

### 주요 모듈

```text
Windows Client
 ┣ Overlay Editor
 ┣ Overlay Renderer
 ┣ Overlay Serializer
 ┣ Target Window Tracker
 ┣ Remote Control UI
 ┣ Hotkey Manager
 ┣ Local File Manager
 ┗ Server Sync Module
```

### 기본 핫키

현재 확정 기준:

```text
ALT + SHIFT + S
→ 현재 포커스된 게임 또는 대상 창의 오버레이 토글
```

기존에 언급되었던 `ALT + SHIFT + A` 전체 오버레이 종료는 현재 구조에서는 제외한다.
이유는 msp overlay가 기본적으로 백그라운드에서 실행되며, 특정 게임 또는 대상 창 단위로 오버레이가 개별 생성·종료되는 구조이기 때문이다.

### 참고 문서

```text
msp_overlay_windows_client_spec.md
msp_overlay_json_schema_spec.md
msp_overlay_api_contract.md
```

---

## 5.4 Android Client

Android Client는 모바일 환경에서 전체 화면 기준 오버레이를 출력하는 클라이언트다.

### 주요 역할

* 전체 화면 기준 오버레이 출력
* Notification 기반 오버레이 ON/OFF 제어
* 서버 라이브러리 조회
* 오버레이 다운로드
* JSON 기반 오버레이 렌더링
* Android 권한 처리

Android는 Windows처럼 특정 창을 추적하지 않는다.
모바일 환경에서는 실행 중인 앱 추적보다 **전체 화면 기준 오버레이 출력**이 더 현실적이다.

### 기술 스택

```text
Java
Android Studio
Gradle
```

### 참고 문서

```text
msp_overlay_android_client_spec.md
msp_overlay_json_schema_spec.md
msp_overlay_api_contract.md
```

---

## 5.5 Database

Database는 검색, 권한, 라이브러리, 카테고리 관리를 위한 구조화 데이터를 저장한다.

### 주요 저장 대상

* 사용자 정보
* 플랫폼 정보
* 게임 정보
* 오버레이 메타데이터
* 사용자 라이브러리 매핑
* DB 기준 생성/수정 시각

### DBMS

```text
PostgreSQL
```

### 주요 테이블

```text
users
platforms
games
overlays
user_libraries
```

### 참고 문서

```text
msp_overlay_db_spec.md
msp_overlay_backend_spec.md
```

---

## 5.6 File Storage

File Storage는 실제 오버레이 원본 파일과 썸네일을 저장한다.

### 저장 대상

```text
overlay.json
thumbnail.png
```

MVP 단계에서는 JSON 원문을 DB에 저장하는 방식도 가능하다.
다만 장기적으로는 다음 구조가 더 적합하다.

```text
DB
 → 검색용 메타데이터 저장

File Storage
 → overlay.json
 → thumbnail.png
```

### 참고 문서

```text
msp_overlay_storage_spec.md
msp_overlay_json_schema_spec.md
msp_overlay_backend_spec.md
```

---

## 5.7 Overlay JSON Format

Overlay JSON은 msp overlay의 핵심 데이터 포맷이다.

서버는 업로드 시 이 JSON 구조를 검증해야 하며, Windows / Android 클라이언트는 이 JSON을 기반으로 실제 오버레이를 렌더링한다.

### 현재 MVP 기준 주요 필드

```text
schemaVersion
overlayId
name
platform
game
canvas
overlaySettings
elements
meta
```

### 현재 지원 요소

```text
rect
circle
line
```

### 현재 제외 요소

```text
image
text
```

### 참고 문서

```text
msp_overlay_json_schema_spec.md
msp_overlay_windows_client_spec.md
msp_overlay_android_client_spec.md
msp_overlay_backend_spec.md
```

---

# 6. Overlay JSON 명세

## 6.1 저장 단위

오버레이 1개는 기본적으로 아래 단위로 저장한다.

```text
overlay.json
thumbnail.png
```

현재 기준에서는 `image` 요소를 지원하지 않으므로 `assets` 폴더는 제외한다.

---

## 6.2 JSON 최상위 구조

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
```

---

## 6.3 최상위 필드

| 필드                |     타입 | 필수 | 설명               |
| ----------------- | -----: | -: | ---------------- |
| `schemaVersion`   | string |  Y | JSON 구조 버전       |
| `overlayId`       | string |  Y | 오버레이 고유 ID       |
| `name`            | string |  Y | 오버레이 이름          |
| `platform`        | string |  Y | 적용 플랫폼           |
| `game`            | object |  N | 게임 분류 정보         |
| `canvas`          | object |  Y | 기준 해상도 정보        |
| `overlaySettings` | object |  Y | 오버레이 전체 설정       |
| `elements`        |  array |  Y | 오버레이 요소 목록       |
| `meta`            | object |  Y | JSON 파일 생성/수정 정보 |

---

## 6.4 platform 허용값

```text
windows
android
```

---

## 6.5 canvas

```json
"canvas": {
  "baseWidth": 1920,
  "baseHeight": 1080
}
```

| 필드           |     타입 | 필수 | 설명       |
| ------------ | -----: | -: | -------- |
| `baseWidth`  | number |  Y | 편집 기준 너비 |
| `baseHeight` | number |  Y | 편집 기준 높이 |

현재는 다음 항목을 제외한다.

```text
scaleMode
unit
origin
```

---

## 6.6 overlaySettings

```json
"overlaySettings": {
  "opacity": 0.85
}
```

| 필드        |     타입 | 필수 |        범위 | 설명          |
| --------- | -----: | -: | --------: | ----------- |
| `opacity` | number |  Y | 0.0 ~ 1.0 | 오버레이 전체 투명도 |

현재 JSON에서 제외하는 실행 정책:

```text
visible
clickThrough
followTargetWindow
showOnlyWhenTargetFocused
alwaysOnTop
anchorPreset
offsetX
offsetY
```

위 항목들은 JSON 저장 데이터가 아니라 클라이언트 실행 정책으로 관리한다.

---

## 6.7 elements

현재 지원 요소는 다음 3개다.

```text
rect
circle
line
```

지원 제외:

```text
image
text
```

---

### 6.7.1 rect

```json
{
  "id": "el_001",
  "type": "rect",
  "x": 860,
  "y": 490,
  "width": 200,
  "height": 100,
  "rotation": 0,
  "opacity": 0.6,
  "zIndex": 1,
  "visible": true,
  "locked": false,
  "fillColor": "#000000",
  "strokeColor": "#FFFFFF",
  "strokeWidth": 2,
  "cornerRadius": 12
}
```

### rect 필드

```text
id
type
x
y
width
height
rotation
opacity
zIndex
visible
locked
fillColor
strokeColor
strokeWidth
cornerRadius
```

---

### 6.7.2 circle

```json
{
  "id": "el_002",
  "type": "circle",
  "x": 910,
  "y": 490,
  "width": 100,
  "height": 100,
  "rotation": 0,
  "opacity": 0.7,
  "zIndex": 2,
  "visible": true,
  "locked": false,
  "fillColor": "#000000",
  "strokeColor": "#FFFFFF",
  "strokeWidth": 2
}
```

### circle 필드

```text
id
type
x
y
width
height
rotation
opacity
zIndex
visible
locked
fillColor
strokeColor
strokeWidth
```

---

### 6.7.3 line

```json
{
  "id": "el_003",
  "type": "line",
  "x1": 500,
  "y1": 500,
  "x2": 700,
  "y2": 500,
  "opacity": 1.0,
  "zIndex": 3,
  "visible": true,
  "locked": false,
  "strokeColor": "#FFFFFF",
  "strokeWidth": 3,
  "dashStyle": "solid"
}
```

### line 필드

```text
id
type
x1
y1
x2
y2
opacity
zIndex
visible
locked
strokeColor
strokeWidth
dashStyle
```

### dashStyle 허용값

```text
solid
dash
dot
```

---

## 6.8 meta

```json
"meta": {
  "createdAt": "2026-04-15T19:30:00+09:00",
  "updatedAt": "2026-04-15T19:45:00+09:00"
}
```

| 필드          |     타입 | 필수 | 설명               |
| ----------- | -----: | -: | ---------------- |
| `createdAt` | string |  Y | JSON 파일 최초 생성 시각 |
| `updatedAt` | string |  Y | JSON 파일 최종 수정 시각 |

형식은 ISO-8601을 사용한다.

---

# 7. DB 설계 개요

## 7.1 DB 저장 원칙

오버레이 데이터는 두 층으로 나눈다.

```text
DB
 → 검색 / 권한 / 라이브러리 / 카테고리용 구조화 데이터

JSON
 → 실제 렌더링 가능한 오버레이 원본 데이터
```

즉, DB는 오버레이를 그리는 데 필요한 모든 element를 세부 테이블로 쪼개지 않는다.
MVP에서는 **JSON 원문 저장 + 메타데이터 분리** 구조가 적합하다.

---

## 7.2 주요 테이블

```text
users
platforms
games
overlays
user_libraries
```

---

## 7.3 users

Google OAuth 기반 사용자 정보 저장.

### 주요 필드

```text
id
oauth_provider
oauth_provider_user_id
email
name
profile_image_url
created_at
updated_at
```

### 제약

```text
(oauth_provider, oauth_provider_user_id) unique
```

---

## 7.4 platforms

오버레이 대상 플랫폼 저장.

### 초기 데이터

```text
Windows / windows
Android / android
```

### 주요 필드

```text
id
name
slug
is_active
created_at
updated_at
```

---

## 7.5 games

플랫폼별 게임 카테고리 저장.

### 주요 필드

```text
id
platform_id
slug
display_name
is_active
created_at
updated_at
```

### 예시

```text
minecraft
valorant
overwatch
pubg-mobile
```

---

## 7.6 overlays

오버레이 메타데이터와 JSON 참조 정보를 저장하는 핵심 테이블.

### 주요 필드

```text
id
overlay_id
code
name
description
platform_id
game_id
author_user_id
schema_version
canvas_base_width
canvas_base_height
opacity
json_content
json_path
thumbnail_url
created_at
updated_at
```

### 설명

| 필드              | 설명                 |
| --------------- | ------------------ |
| `overlay_id`    | JSON 내부의 overlayId |
| `code`          | 웹 검색용 6자리 코드       |
| `json_content`  | JSON 원문 저장         |
| `json_path`     | JSON 파일 저장 경로      |
| `thumbnail_url` | 썸네일 경로             |
| `created_at`    | DB 레코드 생성 시각       |
| `updated_at`    | DB 레코드 수정 시각       |

---

## 7.7 user_libraries

사용자가 저장한 오버레이 목록.

### 주요 필드

```text
id
user_id
overlay_id
created_at
```

### 제약

```text
(user_id, overlay_id) unique
```

---

# 8. 날짜 저장 기준

JSON과 DB 모두 날짜를 가진다.
하지만 의미가 다르다.

## 8.1 JSON 날짜

```text
meta.createdAt
meta.updatedAt
```

의미:

```text
오버레이 파일 자체의 생성/수정 시각
```

## 8.2 DB 날짜

```text
created_at
updated_at
```

의미:

```text
서버 DB 레코드의 생성/수정 시각
```

## 8.3 예시

사용자가 다음 흐름으로 작업했다고 가정한다.

```text
4월 1일 로컬에서 오버레이 생성
4월 10일 로컬에서 오버레이 수정
4월 15일 서버 업로드
```

이 경우:

```text
JSON meta.createdAt = 4월 1일
JSON meta.updatedAt = 4월 10일
DB created_at = 4월 15일
DB updated_at = 4월 15일
```

---

# 9. Backend API 개요

## 9.1 인증 API

```text
GET /api/auth/me
```

현재 로그인한 사용자 정보를 조회한다.

---

## 9.2 플랫폼 API

```text
GET /api/platforms
```

플랫폼 목록을 조회한다.

---

## 9.3 게임 API

```text
GET /api/games?platform=windows
```

플랫폼별 게임 목록을 조회한다.

---

## 9.4 오버레이 목록 API

```text
GET /api/overlays
```

### 쿼리 파라미터

```text
page
size
code
keyword
platform
game
```

### 반환 주요 정보

```text
id
overlayId
code
name
description
platform
game
thumbnailUrl
author
isSaved
createdAt
updatedAt
```

---

## 9.5 오버레이 상세 API

```text
GET /api/overlays/{id}
```

### 반환 주요 정보

```text
metadata
overlay json
isSaved
```

---

## 9.6 오버레이 업로드 API

```text
POST /api/overlays
```

### 권한

```text
로그인 필요
```

### 입력 방식

```text
multipart/form-data
```

### 입력 필드

```text
name
description
platform
gameId 또는 game
code
overlayJson
thumbnail
```

### 서버 처리 순서

```text
1. 로그인 사용자 확인
2. code 중복 검사
3. 플랫폼 / 게임 유효성 검사
4. JSON 구조 검증
5. 메타데이터 추출
6. JSON 저장
7. 썸네일 저장
8. DB 저장
```

---

## 9.7 라이브러리 저장 API

```text
POST /api/library
```

### 권한

```text
로그인 필요
```

### 처리

```text
1. 로그인 사용자 확인
2. 대상 overlay 존재 확인
3. 중복 저장 여부 확인
4. user_libraries 저장
```

---

## 9.8 라이브러리 조회 API

```text
GET /api/library
```

로그인한 사용자의 저장 오버레이 목록을 조회한다.

---

# 10. JSON 검증 규칙

## 10.1 필수 검증 항목

```text
schemaVersion 존재 여부
overlayId 존재 여부
name 존재 여부
platform 존재 여부
canvas.baseWidth 존재 여부
canvas.baseHeight 존재 여부
overlaySettings.opacity 존재 여부
elements 배열 여부
meta.createdAt 존재 여부
meta.updatedAt 존재 여부
```

---

## 10.2 값 검증

```text
platform은 windows 또는 android만 허용
opacity는 0.0 ~ 1.0
elements[].type은 rect, circle, line만 허용
schemaVersion은 지원 버전만 허용
code는 6자리 규칙 만족
```

---

## 10.3 code 규칙

```text
^[A-Z0-9]{6}$
```

---

# 11. 전체 데이터 흐름

## 11.1 오버레이 제작 흐름

```text
Windows Client
 → 사용자가 오버레이 제작
 → overlay.json 생성
 → thumbnail.png 생성
 → 로컬 저장
```

---

## 11.2 오버레이 업로드 흐름

```text
Web 또는 Windows Client
 → 로그인 확인
 → overlay.json 업로드
 → Backend JSON 검증
 → 메타데이터 추출
 → DB 저장
 → JSON / Thumbnail 저장
```

---

## 11.3 웹 탐색 흐름

```text
Web Frontend
 → 플랫폼 / 게임 / 코드 검색
 → Backend API 요청
 → DB 메타데이터 조회
 → 카드형 목록 표시
 → 상세 페이지 표시
```

---

## 11.4 라이브러리 저장 흐름

```text
User
 → 웹에서 오버레이 선택
 → 라이브러리 저장 요청
 → Backend 사용자 확인
 → user_libraries 저장
 → Web / Windows / Android에서 재사용
```

---

## 11.5 클라이언트 적용 흐름

```text
Windows / Android Client
 → 내 라이브러리 조회
 → 오버레이 선택
 → overlay.json 다운로드
 → JSON 파싱
 → 플랫폼 방식에 맞춰 렌더링
```

---

# 12. 인증 정책

## 12.1 인증 방식

```text
Google OAuth + JWT
```

---

## 12.2 비로그인 사용자 가능 기능

```text
오버레이 목록 조회
오버레이 상세 조회
플랫폼 목록 조회
게임 목록 조회
```

---

## 12.3 로그인 사용자 가능 기능

```text
내 정보 조회
오버레이 업로드
라이브러리 저장
내 라이브러리 조회
```

---

# 13. MVP 범위

## 13.1 MVP 포함

```text
오버레이 목록 조회
6자리 코드 검색
플랫폼 / 게임 필터
Google 로그인
JWT 인증
오버레이 업로드
overlay.json 검증
썸네일 저장
내 라이브러리 저장
내 라이브러리 조회
Windows Client 로컬 JSON 저장 / 불러오기
Windows Client JSON 기반 렌더링
Android Client 전체 화면 오버레이 테스트
```

---

## 13.2 MVP 제외

```text
좋아요
댓글
신고
관리자 검수
추천 알고리즘
다운로드 통계
오버레이 버전 관리
복잡한 에셋 관리
image element
text element
iOS 지원
AI 기반 추천
사용자 피드백 분석 자동화
```

---

# 14. 구현 우선순위

현재 구현 우선순위는 다음과 같다.

```text
1. Backend Server
2. Web Frontend
3. Windows Client 연동
4. Android Client 연동
```

초기에는 Backend와 Web을 먼저 구성하여 아래 흐름을 완성한다.

```text
오버레이 목록 조회
 → 코드 검색
 → 플랫폼 / 게임 필터
 → 로그인
 → 업로드
 → 라이브러리 저장
 → 라이브러리 조회
```

그 후 Windows / Android 클라이언트가 동일 API와 Overlay JSON 구조를 재사용한다.

---

# 15. 세부 명세서 목록

| 문서명                                  | 역할                      |
| ------------------------------------ | ----------------------- |
| `msp_overlay_system_spec.md`         | 전체 시스템 큰 틀과 문서 방향 안내    |
| `msp_overlay_backend_spec.md`        | 백엔드 구현 명세               |
| `msp_overlay_frontend_spec.md`       | 웹 프론트엔드 구현 명세           |
| `msp_overlay_api_contract.md`        | 프론트/클라이언트/서버 간 API 계약   |
| `msp_overlay_db_spec.md`             | DB 테이블 및 관계 명세          |
| `msp_overlay_json_schema_spec.md`    | 오버레이 JSON 구조 및 검증 명세    |
| `msp_overlay_windows_client_spec.md` | Windows 클라이언트 구현 명세     |
| `msp_overlay_android_client_spec.md` | Android 클라이언트 구현 명세     |
| `msp_overlay_storage_spec.md`        | 파일 저장소 및 업로드 구조 명세      |
| `msp_overlay_ui_reference.md`        | 웹 UI/UX 레퍼런스 및 화면 구성 명세 |

---

# 16. 개발 시 문서 참조 방향

## 16.1 서버 구현 시

```text
1. msp_overlay_backend_spec.md
2. msp_overlay_api_contract.md
3. msp_overlay_db_spec.md
4. msp_overlay_json_schema_spec.md
5. msp_overlay_storage_spec.md
```

확인할 내용:

```text
의존성
패키지 구조
인증 방식
API 목록
DTO 구조
DB 테이블
JSON 검증 규칙
업로드 처리 방식
```

---

## 16.2 프론트엔드 구현 시

```text
1. msp_overlay_frontend_spec.md
2. msp_overlay_ui_reference.md
3. msp_overlay_api_contract.md
4. msp_overlay_json_schema_spec.md
```

확인할 내용:

```text
페이지 구조
라우팅
API 호출 방식
목록 / 상세 / 업로드 / 라이브러리 UI
로그인 상태 처리
오버레이 JSON 업로드 방식
에러 / 로딩 / 빈 상태 처리
```

---

## 16.3 Windows 클라이언트 구현 시

```text
1. msp_overlay_windows_client_spec.md
2. msp_overlay_json_schema_spec.md
3. msp_overlay_api_contract.md
```

확인할 내용:

```text
창 추적 방식
오버레이 편집 구조
JSON 저장 / 불러오기
JSON 렌더링 방식
서버 업로드
라이브러리 다운로드
썸네일 생성
핫키 처리
Remote Control UI
```

---

## 16.4 Android 클라이언트 구현 시

```text
1. msp_overlay_android_client_spec.md
2. msp_overlay_json_schema_spec.md
3. msp_overlay_api_contract.md
```

확인할 내용:

```text
전체 화면 오버레이 방식
Notification 제어
JSON 렌더링
서버 라이브러리 연동
권한 처리
```

---

## 16.5 DB 설계 시

```text
1. msp_overlay_db_spec.md
2. msp_overlay_backend_spec.md
3. msp_overlay_json_schema_spec.md
```

확인할 내용:

```text
users
platforms
games
overlays
user_libraries
JSON 주요 필드 중복 저장 여부
created_at / updated_at 기준
```

---

# 17. 핵심 설계 원칙

```text
1. 전체 시스템은 Overlay JSON을 중심으로 연결한다.
2. Backend는 JSON 원본을 검증하고 메타데이터를 관리한다.
3. Web은 오버레이를 탐색하고 라이브러리에 저장하는 허브 역할을 한다.
4. Windows Client는 오버레이 제작과 게임 창 기준 렌더링을 담당한다.
5. Android Client는 전체 화면 기준 렌더링을 담당한다.
6. DB는 검색, 권한, 라이브러리 관리를 위한 구조화 데이터를 저장한다.
7. JSON은 렌더링 가능한 원본 데이터로 취급한다.
8. 실행 정책은 JSON에 과하게 넣지 않고 클라이언트 정책으로 관리한다.
9. MVP는 단순하게 구성하되, 확장을 막지 않는 구조로 설계한다.
```

---

# 18. 최종 정리

현재 기준에서 msp overlay는 단순 오버레이 실행기가 아니라, 다음 구조를 가진다.

```text
오버레이 제작
 → JSON 저장
 → 서버 업로드
 → 웹 탐색
 → 라이브러리 저장
 → Windows / Android 적용
```

따라서 시스템의 중심은 **Overlay JSON + Backend API**다.

개발 우선순위는 Backend부터 잡는 것이 맞고, 이후 Web Frontend를 붙여서 업로드·조회·라이브러리 흐름을 먼저 완성하는 방향이 적합하다.
Windows Client와 Android Client는 이 구조가 안정화된 뒤 동일 JSON과 API를 재사용하는 방식으로 확장하면 된다.
