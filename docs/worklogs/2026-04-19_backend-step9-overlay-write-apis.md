# Task Summary

구현 순서 9단계의 남은 범위인 Overlay 수정/삭제 API를 추가했다.

# Scope

- `PATCH /api/overlays/{overlayId}` 구현
- `DELETE /api/overlays/{overlayId}` 구현
- 작성자 권한 검증 추가
- overlay.json/thumbnail 부분 교체 및 스토리지 정리 로직 추가
- overlay 삭제 시 라이브러리 매핑과 저장 파일 정리 추가
- 수정/삭제 관련 단위 테스트 추가

# Changed Files

- `src/main/java/com/mspoverlay/domain/overlay/OverlayController.java`
- `src/main/java/com/mspoverlay/domain/overlay/OverlayCommandService.java`
- `src/main/java/com/mspoverlay/domain/overlay/OverlayUpdateRequest.java`
- `src/main/java/com/mspoverlay/domain/overlay/Overlay.java`
- `src/main/java/com/mspoverlay/domain/overlay/OverlayRepository.java`
- `src/main/java/com/mspoverlay/domain/library/UserLibraryRepository.java`
- `src/main/java/com/mspoverlay/infrastructure/storage/OverlayStorageService.java`
- `src/test/java/com/mspoverlay/domain/overlay/OverlayCommandServiceTest.java`
- `src/test/java/com/mspoverlay/domain/overlay/OverlayControllerTest.java`
- `docs/worklogs/2026-04-19_backend-step9-overlay-write-apis.md`
- `docs/worklogs/_index.md`

# Verification Result

- `.\gradlew.bat test --tests "com.mspoverlay.domain.platform.PlatformControllerTest" --tests "com.mspoverlay.domain.game.GameControllerTest" --tests "com.mspoverlay.domain.overlay.OverlayControllerTest" --tests "com.mspoverlay.domain.overlay.OverlayCommandServiceTest"` 통과
- 작성자만 수정/삭제 가능하고, 비작성자는 `FORBIDDEN`이 발생하는지 단위 테스트로 확인
- 수정 시 코드 중복 제외, 플랫폼/게임 정합성, overlayId 변경 금지, JSON 재검증이 적용되는지 확인
- 삭제 시 `user_libraries` 정리와 스토리지 디렉터리 삭제가 함께 호출되는지 확인

# Decisions Made

- 오버레이 수정은 `multipart/form-data` 기반 부분 수정으로 구현했다
- 플랫폼을 바꾸는 경우 기존 JSON과 불일치가 생기지 않도록 `overlayJson` 파일을 함께 요구하도록 제한했다
- overlayId는 파일 교체 시에도 변경 불가로 고정했다
- 삭제 시 DB cascade에만 의존하지 않고 라이브러리 매핑과 스토리지 삭제를 명시적으로 처리했다

# Issues

- 전체 통합 테스트는 Docker/Testcontainers 환경이 필요해 별도 검증이 남아 있다
- 수정 API는 현재 `sort=saved`나 고급 파일 이력 관리는 지원하지 않는다

# Next Steps

- Library 저장/조회/삭제 API 구현
- Overlay 수정/삭제 통합 테스트 추가
- 프론트에서 수정/삭제 플로우 연결 후 실제 수동 검증
