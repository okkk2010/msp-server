# Task Summary

문서의 구현 순서 4단계에 맞춰 공통 응답/예외 처리 구조를 추가했다.

# Scope

- 성공 응답용 공통 래퍼 추가
- 실패 응답용 공통 에러 응답 추가
- 도메인 전반에서 재사용할 에러 코드 enum 추가
- 비즈니스 예외 타입과 전역 예외 처리기 추가
- 인증/검증/도메인 오류가 일관된 JSON 형태로 내려가도록 기반 정리

# Changed Files

- `src/main/java/com/mspoverlay/global/response/ApiResponse.java`
- `src/main/java/com/mspoverlay/global/response/ErrorResponse.java`
- `src/main/java/com/mspoverlay/global/exception/ErrorCode.java`
- `src/main/java/com/mspoverlay/global/exception/BusinessException.java`
- `src/main/java/com/mspoverlay/global/exception/GlobalExceptionHandler.java`

# Verification Result

- 공통 성공 응답이 `success`, `data`, `message` 구조로 반환되도록 구현된 것을 확인
- 공통 실패 응답이 `success`, `code`, `message` 구조로 반환되도록 구현된 것을 확인
- `BusinessException`이 `GlobalExceptionHandler`에서 HTTP 상태와 에러 코드 기반으로 매핑되는 것을 확인
- 이후 단계 통합 테스트에서 `INVALID_OVERLAY_JSON`, `UNAUTHORIZED` 등 공통 예외 응답 구조가 실제로 재사용되는 것을 확인

# Decisions Made

- 컨트롤러마다 개별 응답 DTO를 만들더라도 최상위 응답 형식은 `ApiResponse` / `ErrorResponse`로 통일했다
- 도메인 로직 오류는 `BusinessException + ErrorCode` 조합으로 표현하고, 예외 메시지는 사용자에게 바로 반환 가능한 수준으로 유지했다
- `MethodArgumentNotValidException`, `MethodArgumentTypeMismatchException`, `IllegalArgumentException`도 전역 예외 처리기에 모아 API 응답 포맷을 통일했다

# Issues

- 초기 구현 직후 worklog를 남기지 못해 실제 코드 반영 시점과 문서 기록 시점이 어긋났다

# Next Steps

- 구현 순서 5단계인 User / RefreshToken / Platform / Game / Overlay / UserLibrary 도메인 구성을 진행
- 이후 인증, 업로드, 라이브러리 API에서도 공통 응답/예외 구조를 그대로 재사용
