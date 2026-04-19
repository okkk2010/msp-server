# Task Summary

프론트엔드에서 바로 붙여 테스트할 수 있도록 더미 overlay JSON과 일치하는 임시 데이터를 로컬 PostgreSQL에 삽입했다.

# Scope

- 로컬 PostgreSQL(`msp_overlay`) 상태 확인
- `frontend-overlay-dashboard.json`과 맞는 플랫폼/게임/사용자/오버레이/라이브러리 데이터 삽입
- `json_path`, `thumbnail_path`와 일치하도록 로컬 `storage` 경로에 샘플 파일 배치
- 애플리케이션 코드 변경은 제외

# Changed Files

- `docs/worklogs/2026-04-19_frontend-overlay-seed-data.md`
- `docs/worklogs/_index.md`

# Verification Result

- `psql`로 `platforms`, `games`, `users`, `overlays` 상태를 조회해 로컬 DB 접속 확인
- `ovl_front_dashboard_001` 오버레이가 `windows` / `minecraft` / `front-demo@example.com`과 연결되어 삽입된 것 확인
- `storage/overlays/ovl_front_dashboard_001/overlay.json`
- `storage/overlays/ovl_front_dashboard_001/thumbnail.png`

# Decisions Made

- 재실행 시 중복 삽입을 피하기 위해 게임/사용자/라이브러리는 upsert 또는 조건부 insert 형태로 반영했다
- 오버레이 경로는 업로드 API가 저장하는 형식과 동일한 `/storage/overlays/{overlayId}/...` 규칙을 따랐다
- 프론트 검증 편의를 위해 작성자 계정도 함께 생성했다

# Issues

- DB에는 JSON 본문 자체를 저장하는 컬럼이 없어서, 파일 경로와 실제 저장 파일을 함께 맞춰 두는 방식으로 처리했다

# Next Steps

- 프론트에서 목록/상세 조회 API를 붙여 실제 렌더 경로가 맞는지 확인
- 필요하면 Android 샘플과 추가 오버레이 시드도 같은 방식으로 확장
