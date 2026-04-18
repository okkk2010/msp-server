# msp overlay API Contract Specification

## 1. 공통 규칙

- 개발 Base URL: `http://localhost:8080`
- 운영 Base URL: `https://{운영 도메인}`
- 운영 환경 HTTPS 필수
- 인증 방식: JWT Bearer Token

```http
Authorization: Bearer {accessToken}
```

## 2. 공통 응답

```json
{
  "success": true,
  "data": {},
  "message": "ok"
}
```

## 3. Auth API

### GET /api/auth/google

Google OAuth 로그인 시작.

### GET /api/auth/google/callback

Google OAuth 콜백 처리 후 JWT 발급.

### GET /api/auth/me

현재 로그인 사용자 조회. 인증 필요.

### POST /api/auth/refresh

Access Token 재발급.

```json
{
  "refreshToken": "refresh-token-value"
}
```

정책:

- Access Token: 30분
- Refresh Token: 14일
- Refresh Token은 DB에 저장

### POST /api/auth/logout

로그아웃. 인증 필요. DB의 Refresh Token을 삭제한다.

## 4. Platform API

### GET /api/platforms

플랫폼 목록 조회. 인증 불필요.

## 5. Game API

### GET /api/games?platform=windows

게임 목록 조회. 인증 불필요.

### PATCH /api/overlays/{overlayId}/game

오버레이 게임 정보 수정. 인증 필요. 작성자만 가능.

```json
{
  "gameId": 10
}
```

`gameId`를 `null`로 보내면 게임 분류를 제거한다.

## 6. Overlay API

### GET /api/overlays

목록 조회. 인증 불필요.

쿼리 파라미터:

- page
- size
- keyword
- platform
- game
- code

### GET /api/overlays/{overlayId}

상세 조회. 인증 불필요.

### POST /api/overlays

오버레이 업로드. 인증 필요.

```http
Content-Type: multipart/form-data
```

multipart 필드:

| 필드 | 필수 | 설명 |
|---|---|---|
| name | Y | 오버레이 이름 |
| description | N | 설명 |
| platform | Y | windows/android |
| gameId | N | 게임 ID |
| code | Y | 6자리 코드 |
| overlayJson | Y | overlay.json |
| thumbnail | Y | thumbnail.png |

### PATCH /api/overlays/{overlayId}

오버레이 수정. 인증 필요. 작성자만 가능.

수정 가능 항목:

- name
- description
- platform
- gameId
- code
- overlayJson
- thumbnail

파일이 교체되면 기존 파일은 삭제한다.

### DELETE /api/overlays/{overlayId}

오버레이 삭제. 인증 필요. 작성자만 가능.

정책:

- hard delete
- user_libraries 매핑 삭제
- overlay.json 즉시 삭제
- thumbnail.png 즉시 삭제

## 7. Library API

### GET /api/library

내 라이브러리 조회. 인증 필요.

### POST /api/library

라이브러리 저장. 인증 필요.

```json
{
  "overlayId": 25
}
```

### DELETE /api/library/{overlayId}

라이브러리 삭제. 인증 필요. 원본 오버레이는 삭제하지 않는다.

## 8. Swagger 정책

- local/dev: 활성화
- prod: 비활성화
