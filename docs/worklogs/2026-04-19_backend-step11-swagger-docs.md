# Task Summary

구현 순서 11단계에 맞춰 Swagger/OpenAPI 문서 설정을 추가하고 운영 프로필에서 비활성화되도록 구성했다.

# Scope

- OpenAPI 기본 정보 및 JWT Bearer 보안 스키마 설정 추가
- Auth/Platform/Game/Overlay/Library 컨트롤러에 Swagger 태그와 요약 설명 추가
- `prod` 프로필에서 Swagger와 API docs 비활성화 설정 추가
- 기존 컨트롤러 테스트로 컴파일/문서 어노테이션 영향 검증

# Changed Files

- `src/main/java/com/mspoverlay/global/config/OpenApiConfig.java`
- `src/main/java/com/mspoverlay/domain/auth/AuthController.java`
- `src/main/java/com/mspoverlay/domain/platform/PlatformController.java`
- `src/main/java/com/mspoverlay/domain/game/GameController.java`
- `src/main/java/com/mspoverlay/domain/overlay/OverlayController.java`
- `src/main/java/com/mspoverlay/domain/library/LibraryController.java`
- `src/main/resources/application-prod.yml`
- `docs/worklogs/2026-04-19_backend-step11-swagger-docs.md`
- `docs/worklogs/_index.md`

# Verification Result

- `.\gradlew.bat test --tests "com.mspoverlay.domain.platform.PlatformControllerTest" --tests "com.mspoverlay.domain.game.GameControllerTest" --tests "com.mspoverlay.domain.overlay.OverlayControllerTest" --tests "com.mspoverlay.domain.library.LibraryControllerTest"` 통과
- 현재 세션 시점에는 런타임 서버가 내려가 있어 `/v3/api-docs`, `/swagger-ui/index.html` 실접속 확인은 진행하지 못함

# Decisions Made

- OpenAPI 전역 보안 스키마는 `bearerAuth` HTTP Bearer JWT 형식으로 통일했다
- 운영 환경에서는 `application-prod.yml`로 Swagger UI와 API docs를 명시적으로 비활성화했다
- 문서 가독성을 위해 주요 컨트롤러 단위로 `@Tag`, 엔드포인트 단위로 `@Operation`을 부여했다

# Issues

- 문서 엔드포인트 실접속 검증은 서버 재기동 후 다시 확인이 필요하다

# Next Steps

- 서버 실행 후 `/v3/api-docs`, `/swagger-ui/index.html` 접근 확인
- 필요하면 DTO 필드 설명과 예시값(`@Schema`)까지 추가
- Testcontainers 기반 통합 테스트 정리
