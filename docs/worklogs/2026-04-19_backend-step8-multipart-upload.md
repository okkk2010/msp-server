# Task Summary

문서의 구현 순서 8단계에 맞춰 multipart/form-data 기반 오버레이 업로드와 로컬 파일 저장 구성을 추가했다.

# Scope

- `POST /api/overlays` 업로드 API 추가
- 업로드 요청 DTO와 응답 DTO 추가
- 인증 사용자 기준 오버레이 저장 서비스 추가
- overlay.json / thumbnail.png 로컬 파일 저장 서비스 추가
- 저장 경로 설정용 storage properties 추가
- 업로드 통합 테스트 추가
- 수동 테스트용 샘플 JSON/PNG 파일과 README 추가
- 생성되는 `storage/` 디렉터리 ignore 처리

# Changed Files

- `.gitignore`
- `src/main/resources/application.yml`
- `src/main/java/com/mspoverlay/global/config/StorageProperties.java`
- `src/main/java/com/mspoverlay/global/config/StorageConfig.java`
- `src/main/java/com/mspoverlay/domain/overlay/OverlayController.java`
- `src/main/java/com/mspoverlay/domain/overlay/OverlayUploadRequest.java`
- `src/main/java/com/mspoverlay/domain/overlay/OverlayUploadResponse.java`
- `src/main/java/com/mspoverlay/domain/overlay/OverlayUploadService.java`
- `src/main/java/com/mspoverlay/infrastructure/storage/OverlayStorageService.java`
- `src/main/java/com/mspoverlay/infrastructure/storage/StoredOverlayFiles.java`
- `src/test/java/com/mspoverlay/domain/overlay/OverlayUploadIntegrationTest.java`
- `docs/manual-test-data/valid-overlay.json`
- `docs/manual-test-data/invalid-overlay-missing-meta.json`
- `docs/manual-test-data/thumbnail.png`
- `docs/manual-test-data/README.md`

# Verification Result

- `POST /api/overlays`가 `multipart/form-data`로 `name`, `description`, `platform`, `gameId`, `code`, `overlayJson`, `thumbnail`을 받도록 구현된 것을 확인
- 업로드 시 JSON Schema 검증, 플랫폼/게임 확인, 코드 중복 검사, 파일 저장, DB 저장이 순서대로 수행되도록 확인
- 파일이 `/storage/overlays/{overlayId}/overlay.json`, `/storage/overlays/{overlayId}/thumbnail.png` 형태로 저장되도록 확인
- `OverlayUploadIntegrationTest`에서 성공 업로드와 `INVALID_OVERLAY_JSON` 실패 시나리오를 Testcontainers + MockMvc로 검증
- `gradlew.bat test` 실행으로 전체 테스트가 통과함을 확인

# Decisions Made

- 저장 경로는 기본값 `storage`를 사용하고 `app.storage.base-path` 설정으로 바꿀 수 있게 했다
- 사용자 원본 파일명을 저장명으로 쓰지 않고 `overlay.json`, `thumbnail.png` 고정 이름을 사용했다
- JSON Schema 검증은 저장 전에 수행하고, 저장 도중 예외가 나면 생성된 디렉터리를 정리하도록 롤백성 처리를 넣었다
- 수동 테스트를 바로 할 수 있도록 샘플 JSON/PNG와 `curl.exe` 예시를 함께 문서화했다

# Issues

- Spring Boot 4 테스트 환경에서 `AutoConfigureMockMvc` 패키지 차이를 피하기 위해 `WebApplicationContext + springSecurityFilterChain` 조합으로 통합 테스트를 구성했다
- 업로드 통합 테스트 초기에 Security filter chain이 빠져 `@AuthenticationPrincipal`이 null이 되는 문제가 있었지만 필터 주입 후 해결했다

# Next Steps

- 구현 순서 9단계인 오버레이 조회/수정/삭제 API를 추가
- 수정/삭제 시 기존 파일 교체 및 정리 로직을 storage 서비스에 확장
