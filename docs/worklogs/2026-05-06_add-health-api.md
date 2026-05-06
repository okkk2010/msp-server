# Task Summary

- API 테스트용 health check 엔드포인트를 추가했다.

# Scope

- 기존 API 응답 포맷에 맞춘 `GET /api/health` 추가
- 인증 없이 호출 가능하도록 보안 설정 반영
- health 컨트롤러 단위 테스트 추가
- 작업로그 및 worklog index 갱신

# Changed Files

- `src/main/java/com/mspoverlay/global/health/HealthController.java`
- `src/main/java/com/mspoverlay/global/health/HealthResponse.java`
- `src/main/java/com/mspoverlay/global/security/SecurityConfig.java`
- `src/test/java/com/mspoverlay/global/health/HealthControllerTest.java`
- `docs/worklogs/_index.md`
- `docs/worklogs/2026-05-06_add-health-api.md`

# Verification Result

- `GET /api/health` 응답이 `success=true`, `data.status=ok`, `message=ok` 형태로 반환되는지 단위 테스트로 확인했다.
- 실행 명령: `.\gradlew.bat test --tests com.mspoverlay.global.health.HealthControllerTest`
- 결과: 성공

# Decisions Made

- 테스트용 health API도 기존 `ApiResponse` 래퍼를 사용하도록 했다.
- 엔드포인트 경로는 API prefix 관례에 맞춰 `/api/health`로 정했다.
- 헬스 체크는 외부 API 테스트 목적이므로 `GET /api/health`만 `permitAll`로 열었다.

# Issues

- Gradle wrapper 다운로드가 필요해 최초 테스트 실행은 sandbox 네트워크 제한으로 실패했고, 승인 후 재실행했다.
- 테스트용 Gradle 캐시 `.gradle-codex/`는 일부 파일이 Java 프로세스에 의해 잠겨 즉시 전체 삭제되지 않아 로컬 git exclude에만 제외 처리했다.

# Next Steps

- 필요하면 실행 중인 Java/IDE 프로세스를 종료한 뒤 `.gradle-codex/` 임시 캐시를 삭제한다.
