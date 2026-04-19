# Task Summary

프론트에서 바로 붙여 테스트할 수 있도록 `ovl_front_dashboard_001` 임시 오버레이 데이터를 로컬 DB와 스토리지 경로에 다시 맞춰 넣었다.

# Scope

- 로컬 PostgreSQL(`msp_overlay`)에 프론트 테스트용 오버레이 레코드 upsert
- 오버레이를 테스트 사용자 라이브러리에 연결
- `storage/overlays/ovl_front_dashboard_001/` 아래 `overlay.json`, `thumbnail.png` 재배치
- 애플리케이션 코드 변경 없이 테스트 데이터만 정리

# Changed Files

- `docs/worklogs/2026-04-19_frontend-overlay-seed-refresh.md`
- `docs/worklogs/_index.md`
- `storage/overlays/ovl_front_dashboard_001/overlay.json`
- `storage/overlays/ovl_front_dashboard_001/thumbnail.png`

# Verification Result

- `psql`로 `overlays`, `user_libraries`를 조회해 `ovl_front_dashboard_001` 레코드와 라이브러리 연결을 확인했다.
- `json_path`가 `/storage/overlays/ovl_front_dashboard_001/overlay.json`으로 저장된 것을 확인했다.
- `thumbnail_path`가 `/storage/overlays/ovl_front_dashboard_001/thumbnail.png`으로 저장된 것을 확인했다.
- 로컬 파일 경로 `storage/overlays/ovl_front_dashboard_001/overlay.json`
- 로컬 파일 경로 `storage/overlays/ovl_front_dashboard_001/thumbnail.png`

# Decisions Made

- 기존 동일 `overlay_id`가 있을 수 있어 `overlay_id` 기준 upsert로 반영했다.
- 프론트에서 바로 노출 테스트할 수 있도록 기존 샘플 코드 `FD0001`과 오버레이 ID를 유지했다.
- 라이브러리 저장도 함께 맞춰서 로그인 후 바로 내 라이브러리 화면에서 확인할 수 있게 했다.

# Issues

- 현재 작업은 테스트 데이터 주입만 포함하며, 서버가 `/storage/**`를 실제로 외부에 서빙하는지는 런타임 설정과 서버 기동 상태에 따라 별도 확인이 필요하다.

# Next Steps

- 프론트에서 오버레이 목록과 상세 API를 호출해 해당 레코드가 노출되는지 확인
- 필요하면 추가 샘플 오버레이를 같은 방식으로 2~3개 더 확장
