# Task Summary

구현 순서 9단계 중 조회 영역을 우선 반영해 Platform/Game/Overlay 조회 API와 로컬 스토리지 정적 파일 서빙을 추가했다.

# Scope

- `GET /api/platforms` 구현
- `GET /api/games?platform=...` 구현
- `GET /api/overlays` 페이지네이션/필터 조회 구현
- `GET /api/overlays/{overlayId}` 상세 조회 구현
- `/storage/**` 정적 리소스 서빙 및 보안 공개 설정
- 조회 컨트롤러 단위 테스트 추가

# Changed Files

- `src/main/java/com/mspoverlay/domain/platform/PlatformController.java`
- `src/main/java/com/mspoverlay/domain/platform/PlatformService.java`
- `src/main/java/com/mspoverlay/domain/platform/PlatformResponse.java`
- `src/main/java/com/mspoverlay/domain/game/GameController.java`
- `src/main/java/com/mspoverlay/domain/game/GameService.java`
- `src/main/java/com/mspoverlay/domain/game/GameResponse.java`
- `src/main/java/com/mspoverlay/domain/overlay/OverlayController.java`
- `src/main/java/com/mspoverlay/domain/overlay/OverlayQueryService.java`
- `src/main/java/com/mspoverlay/domain/overlay/OverlayRepository.java`
- `src/main/java/com/mspoverlay/domain/overlay/OverlaySummaryResponse.java`
- `src/main/java/com/mspoverlay/domain/overlay/OverlayDetailResponse.java`
- `src/main/java/com/mspoverlay/domain/overlay/OverlayAuthorResponse.java`
- `src/main/java/com/mspoverlay/global/response/PageResponse.java`
- `src/main/java/com/mspoverlay/global/config/StorageResourceConfig.java`
- `src/main/java/com/mspoverlay/global/security/SecurityConfig.java`
- `src/test/java/com/mspoverlay/domain/platform/PlatformControllerTest.java`
- `src/test/java/com/mspoverlay/domain/game/GameControllerTest.java`
- `src/test/java/com/mspoverlay/domain/overlay/OverlayControllerTest.java`
- `docs/worklogs/2026-04-19_backend-step9-read-apis.md`
- `docs/worklogs/_index.md`

# Verification Result

- `.\gradlew.bat test --tests "com.mspoverlay.domain.platform.PlatformControllerTest" --tests "com.mspoverlay.domain.game.GameControllerTest" --tests "com.mspoverlay.domain.overlay.OverlayControllerTest"` 통과
- 플랫폼/게임/오버레이 목록 및 상세 조회 응답 형식이 컨트롤러 테스트로 검증됨
- `/storage/**` 경로를 Spring MVC 정적 리소스로 매핑하고 보안에서 공개 경로로 허용함

# Decisions Made

- 프론트 API 계약에 맞춰 오버레이 목록 응답은 `PageResponse` 형태로 통일했다
- 오버레이 목록 정렬은 `newest`, `updated` 두 가지를 먼저 지원하고 나머지는 `INVALID_INPUT`으로 처리했다
- 게임 필터는 `slug`와 숫자 ID를 모두 허용하도록 구성했다
- 스토리지 파일 접근은 별도 컨트롤러 대신 `ResourceHandler` 기반 정적 서빙으로 처리했다

# Issues

- 수정/삭제 API와 Library API는 아직 이번 단계 범위에 포함하지 않았다
- 전체 통합 테스트는 Docker/Testcontainers 환경이 필요하므로 별도 확인이 남아 있다

# Next Steps

- Overlay 수정/삭제 API 구현
- Library 저장/조회/삭제 API 구현
- 조회 API에 대한 통합 테스트와 프론트 연동 검증 추가
