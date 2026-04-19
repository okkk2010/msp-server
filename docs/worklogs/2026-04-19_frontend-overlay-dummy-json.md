# Task Summary

프론트엔드에서 오버레이 화면을 바로 붙여 테스트할 수 있도록 다중 요소를 포함한 더미 overlay JSON을 추가했다.

# Scope

- 프론트 렌더 테스트용 수동 샘플 JSON 추가
- 기존 수동 테스트 README에 샘플 파일 안내 추가
- 서버 로직이나 스키마 변경은 제외

# Changed Files

- `docs/manual-test-data/frontend-overlay-dashboard.json`
- `docs/manual-test-data/README.md`
- `docs/worklogs/2026-04-19_frontend-overlay-dummy-json.md`
- `docs/worklogs/_index.md`

# Verification Result

- JSON 구조가 현재 `overlay-schema.json`의 필수 필드와 요소 타입(`rect`, `circle`, `line`)을 모두 만족하도록 작성
- 프론트 렌더 테스트에 필요한 다양한 위치/크기/색상 조합 포함 확인

# Decisions Made

- 최소 검증용 샘플과 분리하기 위해 프론트 전용 샘플 파일을 별도로 추가했다
- 요소 타입별 렌더 확인이 가능하도록 사각형, 원, 선을 모두 포함했다
- 업로드 API와 프론트 미리보기 양쪽에서 재사용 가능하도록 스키마 준수 형태로 작성했다

# Issues

- 없음

# Next Steps

- 프론트에서 실제 렌더 규칙이 정해지면 텍스트, 이미지 등 추가 요소 타입에 맞춰 샘플 확장
- 필요하면 Android 전용 샘플도 별도 추가
