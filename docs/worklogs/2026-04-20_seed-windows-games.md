# Task Summary

- `games` 테이블에 Windows용 1인칭/3인칭 3D 게임 시드 데이터를 추가했다.

# Scope

- 기존 DB 스키마와 시드 방식 확인
- `windows` 플랫폼 기준 게임 15종 추가
- 로컬 PostgreSQL 반영 및 조회 확인

# Changed Files

- `src/main/resources/db/migration/V2__seed_windows_games.sql`
- `docs/worklogs/_index.md`
- `docs/worklogs/2026-04-20_seed-windows-games.md`

# Verification Result

- `platforms` 테이블에 `windows` 플랫폼이 존재함을 확인했다.
- 새 Flyway 마이그레이션으로 게임 15종을 upsert 형식으로 추가했다.
- `psql`로 `games` 테이블을 조회해 요청한 게임들이 입력되었는지 확인했다.

# Decisions Made

- 기존 초기 스키마 파일은 건드리지 않고 `V2` 마이그레이션으로 분리했다.
- 중복 실행에 대비해 `(platform_id, slug)` 기준 `on conflict do update`를 사용했다.
- 요청하신 게임군에 맞춰 Windows 플랫폼용 3D 게임만 포함했다.

# Issues

- Android 플랫폼 게임 데이터는 이번 범위에서 제외했다.

# Next Steps

- 필요하면 Android 게임 시드도 별도 마이그레이션으로 추가한다.
- 프론트 필터 목록에서 새 게임들이 정상 노출되는지 확인한다.
