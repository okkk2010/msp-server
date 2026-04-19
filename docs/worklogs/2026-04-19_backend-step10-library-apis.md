# Task Summary

구현 순서 10단계에 맞춰 Library 저장/조회/삭제 API를 추가했다.

# Scope

- `GET /api/library` 구현
- `POST /api/library` 구현
- `DELETE /api/library/{overlayId}` 구현
- 라이브러리 저장 중복/미존재 예외 처리 추가
- Library 응답 DTO 및 단위 테스트 추가

# Changed Files

- `src/main/java/com/mspoverlay/domain/library/LibraryController.java`
- `src/main/java/com/mspoverlay/domain/library/LibraryService.java`
- `src/main/java/com/mspoverlay/domain/library/LibrarySaveRequest.java`
- `src/main/java/com/mspoverlay/domain/library/LibraryItemResponse.java`
- `src/main/java/com/mspoverlay/domain/library/UserLibraryRepository.java`
- `src/main/java/com/mspoverlay/global/exception/ErrorCode.java`
- `src/test/java/com/mspoverlay/domain/library/LibraryServiceTest.java`
- `src/test/java/com/mspoverlay/domain/library/LibraryControllerTest.java`
- `docs/worklogs/2026-04-19_backend-step10-library-apis.md`
- `docs/worklogs/_index.md`

# Verification Result

- `.\gradlew.bat test --tests "com.mspoverlay.domain.library.LibraryServiceTest" --tests "com.mspoverlay.domain.library.LibraryControllerTest"` 통과
- `.\gradlew.bat test --tests "com.mspoverlay.domain.platform.PlatformControllerTest" --tests "com.mspoverlay.domain.game.GameControllerTest" --tests "com.mspoverlay.domain.overlay.OverlayControllerTest" --tests "com.mspoverlay.domain.overlay.OverlayCommandServiceTest" --tests "com.mspoverlay.domain.library.LibraryServiceTest" --tests "com.mspoverlay.domain.library.LibraryControllerTest"` 통과
- 라이브러리 목록 응답, 중복 저장 방지, 삭제 시 본인 라이브러리만 제거되는 규칙을 단위 테스트로 확인

# Decisions Made

- 라이브러리 삭제 경로의 `overlayId`는 API 계약에 맞춰 오버레이 DB PK 기준으로 처리했다
- 라이브러리 목록 응답은 프론트 스펙의 `LibraryItem` 구조에 맞춰 `libraryId`, `savedAt`, `overlay`를 포함하도록 구성했다
- 중복 저장은 `LIBRARY_ALREADY_SAVED` 예외 코드로 분리했다

# Issues

- 라이브러리 API에 대한 통합 테스트는 아직 추가하지 않았다
- 현재 라이브러리 목록은 별도 검색/필터 없이 저장일 역순만 지원한다

# Next Steps

- Swagger 문서 정리
- Testcontainers 기반 통합 테스트 확장
- 프론트에서 Save/Remove Library 버튼과 실제 연결 검증
