# Task Summary

문서의 구현 순서 5단계에 맞춰 핵심 도메인 엔티티와 리포지토리를 구성했다.

# Scope

- `User`, `RefreshToken`, `Platform`, `Game`, `Overlay`, `UserLibrary` 엔티티 추가
- 각 도메인별 Spring Data JPA 리포지토리 추가
- 공통 생성/수정 시각 베이스 엔티티 추가
- OAuth 공급자 enum 분리
- DB 명세에 맞는 필드/관계 매핑 구성

# Changed Files

- `src/main/java/com/mspoverlay/domain/user/User.java`
- `src/main/java/com/mspoverlay/domain/user/UserRepository.java`
- `src/main/java/com/mspoverlay/domain/user/OAuthProvider.java`
- `src/main/java/com/mspoverlay/domain/auth/RefreshToken.java`
- `src/main/java/com/mspoverlay/domain/auth/RefreshTokenRepository.java`
- `src/main/java/com/mspoverlay/domain/platform/Platform.java`
- `src/main/java/com/mspoverlay/domain/platform/PlatformRepository.java`
- `src/main/java/com/mspoverlay/domain/game/Game.java`
- `src/main/java/com/mspoverlay/domain/game/GameRepository.java`
- `src/main/java/com/mspoverlay/domain/overlay/Overlay.java`
- `src/main/java/com/mspoverlay/domain/overlay/OverlayRepository.java`
- `src/main/java/com/mspoverlay/domain/library/UserLibrary.java`
- `src/main/java/com/mspoverlay/domain/library/UserLibraryRepository.java`
- `src/main/java/com/mspoverlay/global/util/BaseCreatedEntity.java`
- `src/main/java/com/mspoverlay/global/util/BaseTimeEntity.java`

# Verification Result

- 각 엔티티가 Flyway 초기 스키마와 대응되는 테이블/컬럼/관계를 가지도록 매핑된 것을 확인
- `Overlay` 엔티티에 `json_path`, `thumbnail_path`, `schema_version`, `canvas_base_width`, `opacity` 등 명세 필드가 반영된 것을 확인
- 이후 인증/업로드 통합 테스트에서 `User`, `Overlay`, `RefreshToken` 리포지토리가 정상 동작하는 것을 간접 검증
- Testcontainers 기반 전체 테스트 통과 시 JPA 엔티티 매핑과 컨텍스트 로드가 함께 확인됨

# Decisions Made

- 생성 시각만 필요한 테이블과 생성/수정 시각이 모두 필요한 테이블을 분리하기 위해 `BaseCreatedEntity`, `BaseTimeEntity`를 각각 뒀다
- `Overlay`는 JSON 원문을 DB에 저장하지 않고 파일 경로만 저장하도록 엔티티를 설계했다
- 게임은 오버레이 등록 시 선택값이므로 `Overlay.game` 관계를 nullable로 유지했다

# Issues

- 도메인 구성 완료 직후 worklog가 기록되지 않아 이후 단계 구현과 함께 문서화가 지연됐다

# Next Steps

- 구현 순서 6단계인 Google OAuth + JWT 인증 흐름을 추가
- 이후 업로드/수정/삭제 API에서 `Overlay`, `UserLibrary` 도메인을 직접 사용
