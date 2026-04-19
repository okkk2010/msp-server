# Overlay Upload Manual Test

## 준비

1. Docker Postgres 실행
2. 서버 실행
3. Google OAuth 로그인으로 access token 확보

## 샘플 파일

- `valid-overlay.json`
- `frontend-overlay-dashboard.json`
- `invalid-overlay-missing-meta.json`
- `thumbnail.png`

`frontend-overlay-dashboard.json`은 프론트 렌더링 확인용 샘플로, `rect`, `circle`, `line` 요소를 모두 포함한다.

## 업로드 성공 테스트

```powershell
curl.exe -X POST "http://localhost:8080/api/overlays" `
  -H "Authorization: Bearer {ACCESS_TOKEN}" `
  -F "name=Manual Upload Sample" `
  -F "description=manual test" `
  -F "platform=windows" `
  -F "code=ZZ1234" `
  -F "overlayJson=@docs/manual-test-data/valid-overlay.json;type=application/json" `
  -F "thumbnail=@docs/manual-test-data/thumbnail.png;type=image/png"
```

## 업로드 실패 테스트

```powershell
curl.exe -X POST "http://localhost:8080/api/overlays" `
  -H "Authorization: Bearer {ACCESS_TOKEN}" `
  -F "name=Invalid Upload Sample" `
  -F "platform=windows" `
  -F "code=ZZ5678" `
  -F "overlayJson=@docs/manual-test-data/invalid-overlay-missing-meta.json;type=application/json" `
  -F "thumbnail=@docs/manual-test-data/thumbnail.png;type=image/png"
```

정상 기대 결과:

- 성공 요청은 `201`
- 실패 요청은 `400 INVALID_OVERLAY_JSON`
- 성공 후 파일은 `storage/overlays/{overlayId}` 아래에 생성
