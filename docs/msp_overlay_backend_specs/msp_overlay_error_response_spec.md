# msp overlay Error & Response Specification

## 1. 성공 응답

```json
{
  "success": true,
  "data": {},
  "message": "ok"
}
```

## 2. 실패 응답

```json
{
  "success": false,
  "code": "ERROR_CODE",
  "message": "error message"
}
```

## 3. HTTP Status 기준

| Status | 상황 |
|---|---|
| 400 | 잘못된 요청 |
| 401 | 인증 실패 |
| 403 | 권한 없음 |
| 404 | 리소스 없음 |
| 409 | 중복 충돌 |
| 500 | 서버 오류 |

## 4. 에러 코드

| 코드 | Status | 설명 |
|---|---|---|
| INVALID_INPUT | 400 | 입력 오류 |
| INVALID_MULTIPART_REQUEST | 400 | multipart 요청 오류 |
| INVALID_OVERLAY_JSON | 400 | overlay.json 검증 실패 |
| INVALID_THUMBNAIL_FILE | 400 | 썸네일 오류 |
| INVALID_PLATFORM | 400 | 플랫폼 오류 |
| UNAUTHORIZED | 401 | 인증 필요 |
| TOKEN_EXPIRED | 401 | 토큰 만료 |
| INVALID_TOKEN | 401 | 유효하지 않은 토큰 |
| REFRESH_TOKEN_NOT_FOUND | 401 | Refresh Token 없음 |
| FORBIDDEN | 403 | 권한 없음 |
| USER_NOT_FOUND | 404 | 사용자 없음 |
| PLATFORM_NOT_FOUND | 404 | 플랫폼 없음 |
| GAME_NOT_FOUND | 404 | 게임 없음 |
| OVERLAY_NOT_FOUND | 404 | 오버레이 없음 |
| LIBRARY_NOT_FOUND | 404 | 라이브러리 없음 |
| OVERLAY_CODE_DUPLICATED | 409 | 코드 중복 |
| OVERLAY_ALREADY_SAVED | 409 | 이미 저장됨 |
| FILE_SAVE_FAILED | 500 | 파일 저장 실패 |
| FILE_DELETE_FAILED | 500 | 파일 삭제 실패 |
| INTERNAL_SERVER_ERROR | 500 | 서버 오류 |

## 5. 삭제 정책 관련 에러

오버레이 삭제는 hard delete이며 파일도 즉시 삭제한다.

- DB 삭제 실패: 요청 실패
- 파일 삭제 실패: `FILE_DELETE_FAILED`
- MVP에서는 파일 삭제 실패 재시도 큐를 두지 않는다.
