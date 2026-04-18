# msp overlay Deployment Specification

## 1. 환경 구분

- local
- dev
- prod

## 2. Docker Compose 정책

Docker Compose는 초기 개발환경에서 필수로 사용한다.

이유:

- PostgreSQL 설치 환경 차이를 줄인다.
- 새 개발환경 구성 시간을 줄인다.
- Flyway 적용을 검증하기 쉽다.
- 개발 DB를 안정적으로 유지할 수 있다.

## 3. 구성 대상

- backend
- postgres

MVP 초기에는 backend를 로컬에서 직접 실행하고 PostgreSQL만 Docker Compose로 실행해도 된다.

## 4. docker-compose.yml 예시

```yaml
services:
  postgres:
    image: postgres:latest
    container_name: msp-overlay-postgres
    environment:
      POSTGRES_DB: msp_overlay
      POSTGRES_USER: msp_user
      POSTGRES_PASSWORD: msp_password
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

volumes:
  postgres_data:
```

운영 비밀번호는 환경변수 또는 별도 secret으로 관리한다.

## 5. 환경변수

| 이름 | 설명 |
|---|---|
| SPRING_PROFILES_ACTIVE | 실행 환경 |
| DB_URL | DB 연결 URL |
| DB_USERNAME | DB 사용자명 |
| DB_PASSWORD | DB 비밀번호 |
| JWT_SECRET | JWT 서명 키 |
| GOOGLE_CLIENT_ID | Google OAuth Client ID |
| GOOGLE_CLIENT_SECRET | Google OAuth Client Secret |
| STORAGE_ROOT_PATH | 파일 저장 루트 경로 |
| FRONTEND_ORIGIN | CORS 허용 주소 |

## 6. HTTPS 정책

- local/dev: HTTP 허용
- prod: HTTPS 필수

## 7. Swagger 정책

- local/dev: 활성화
- prod: 비활성화

## 8. 파일 저장 경로

```text
/storage/overlays/{overlayId}/overlay.json
/storage/overlays/{overlayId}/thumbnail.png
```

## 9. 배포 전 확인

- prod profile Swagger 비활성화
- HTTPS 적용
- JWT_SECRET 운영값 적용
- Google OAuth redirect URI 운영 도메인 반영
- PostgreSQL 연결 확인
- 파일 저장 경로 권한 확인
- Flyway migration 적용
