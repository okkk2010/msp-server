# Task Summary

문서의 구현 순서 7단계에 맞춰 `overlay.json` JSON Schema 검증기를 추가했다.

# Scope

- overlay JSON Schema 리소스 파일 추가
- networknt 기반 검증 컴포넌트 추가
- 잘못된 JSON 또는 스키마 위반 시 `INVALID_OVERLAY_JSON` 예외 처리 추가
- 정상/실패 케이스를 검증하는 단위 테스트 추가

# Changed Files

- `src/main/resources/jsonschema/overlay-schema.json`
- `src/main/java/com/mspoverlay/infrastructure/jsonschema/OverlayJsonSchemaValidator.java`
- `src/test/java/com/mspoverlay/infrastructure/jsonschema/OverlayJsonSchemaValidatorTest.java`

# Verification Result

- 스키마에 최상위 필수 필드, `platform` 허용값, `opacity` 범위, `rect/circle/line` 허용 타입, `dashStyle`, `meta.createdAt/updatedAt` 형식이 반영된 것을 확인
- `OverlayJsonSchemaValidatorTest`에서 정상 JSON 통과와 필수 필드 누락, 잘못된 플랫폼, 범위 초과 opacity, 지원하지 않는 요소 타입 실패를 검증
- `gradlew.bat test` 실행으로 스키마 검증 단위 테스트와 전체 테스트가 통과함을 확인

# Decisions Made

- 스키마 본문을 자바 코드에 하드코딩하지 않고 `src/main/resources/jsonschema/overlay-schema.json`으로 분리해 재사용성과 가독성을 확보했다
- 검증 실패 메시지는 `BusinessException(ErrorCode.INVALID_OVERLAY_JSON)`으로 통일해 업로드/수정 API에서 바로 재사용할 수 있게 했다
- 검증기 생성자에 테스트 전용 리소스 주입 경로를 남겨 단위 테스트에서 스프링 컨텍스트 없이도 검증할 수 있게 했다

# Issues

- networknt `ValidationMessage` API 차이로 초기에 정렬 기준 메서드 참조가 맞지 않아 컴파일 오류가 발생했지만 수정 후 해결했다
- Spring Boot 4 환경에서 다중 생성자 주입 해석 이슈가 있어 `@Autowired`로 명시해 해결했다

# Next Steps

- 구현 순서 8단계인 multipart/form-data 업로드 API에 이 검증기를 실제로 연결
- 이후 오버레이 수정 API에서도 동일 검증기를 재사용
