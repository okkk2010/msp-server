# msp overlay Auth & Security Specification

## 1. 인증 구조

- Google OAuth Login
- JWT Access Token + Refresh Token

## 2. JWT 정책

| 항목 | 값 |
|---|---|
| Access Token 만료 | 30분 |
| Refresh Token 만료 | 14일 |
| Refresh Token 저장 | DB |
| JWT 라이브러리 | JJWT |

## 3. Refresh Token 정책

```text
로그인 성공
 → Access Token 발급
 → Refresh Token 발급
 → Refresh Token DB 저장
```

```text
로그아웃
 → DB에서 Refresh Token 삭제
```

```text
Access Token 재발급
 → Refresh Token DB 존재 확인
 → 만료 여부 확인
 → Access Token 재발급
```

MVP에서는 Refresh Token 원문 저장 기준으로 구현한다. 추후 운영 보안 강화 시 해시 저장을 검토한다.

## 4. 인증 필요 API

| API | 인증 |
|---|---|
| GET /api/overlays | 불필요 |
| GET /api/overlays/{overlayId} | 불필요 |
| GET /api/platforms | 불필요 |
| GET /api/games | 불필요 |
| GET /api/auth/me | 필요 |
| POST /api/overlays | 필요 |
| PATCH /api/overlays/{overlayId} | 필요 |
| DELETE /api/overlays/{overlayId} | 필요 |
| GET /api/library | 필요 |
| POST /api/library | 필요 |
| DELETE /api/library/{overlayId} | 필요 |
| POST /api/auth/logout | 필요 |

## 5. 권한 정책

- 오버레이 수정: 작성자만 가능
- 오버레이 삭제: 작성자만 가능
- 라이브러리 삭제: 본인 라이브러리만 가능

## 6. HTTPS 정책

- local/dev: HTTP 허용
- prod: HTTPS 필수

## 7. Swagger 정책

- local/dev: 활성화
- prod: 비활성화

## 8. CORS 정책

- local: `http://localhost:5173`
- prod: 운영 Frontend 도메인

운영 도메인은 배포 시 환경변수로 관리한다.
