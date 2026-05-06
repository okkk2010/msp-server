# Task Summary

- `.env` 파일이 없을 때 Docker Compose 명령이 실패하는 문제를 수정했다.

# Scope

- `docker-compose.yml`의 app 서비스에서 필수 `env_file: .env` 참조 제거
- 로컬 Docker 실행에 필요한 기본 app 환경변수 명시
- 작업로그 및 worklog index 갱신

# Changed Files

- `docker-compose.yml`
- `docs/worklogs/_index.md`
- `docs/worklogs/2026-05-06_fix-compose-env-file.md`

# Verification Result

- `docker compose config`로 compose 설정이 `.env` 없이 해석되는지 확인했다.
- `docker compose down -v`로 사용자가 만난 `.env not found` 오류가 해소되는지 확인했다.

# Decisions Made

- `.env` 파일을 새로 만들지 않고, compose 자체가 로컬 기본값으로 동작하도록 했다.
- 컨테이너 내부 app DB 접속 주소는 `localhost:5433`이 아니라 compose 서비스명 기준 `postgres:5432`를 기본값으로 지정했다.
- 운영/개인 설정은 기존처럼 환경변수 또는 `.env` 자동 interpolation으로 덮어쓸 수 있게 `${VAR:-default}` 형식을 유지했다.

# Issues

- 없음

# Next Steps

- 운영 배포에서는 `JWT_SECRET`, CORS, frontend origin 값을 별도 환경변수로 주입한다.
