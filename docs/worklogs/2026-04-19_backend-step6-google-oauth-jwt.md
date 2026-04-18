# Task Summary

문서의 구현 순서 6단계에 맞춰 Google OAuth + JWT 인증 기반을 구성했다.

# Scope

- Spring Security 필터 체인 구성
- JWT 발급/검증용 `JwtTokenProvider` 추가
- JWT 인증 필터와 인증/인가 실패 핸들러 추가
- Google OAuth 로그인 성공 시 사용자 저장 및 토큰 발급 흐름 추가
- `/api/auth/google`, `/api/auth/me`, `/api/auth/refresh`, `/api/auth/logout` API 추가
- Refresh Token DB 저장/삭제 처리 추가

# Changed Files

- `src/main/java/com/mspoverlay/global/security/SecurityConfig.java`
- `src/main/java/com/mspoverlay/global/security/JwtTokenProvider.java`
- `src/main/java/com/mspoverlay/global/security/JwtAuthenticationFilter.java`
- `src/main/java/com/mspoverlay/global/security/RestAuthenticationEntryPoint.java`
- `src/main/java/com/mspoverlay/global/security/RestAccessDeniedHandler.java`
- `src/main/java/com/mspoverlay/global/security/AuthenticatedUser.java`
- `src/main/java/com/mspoverlay/global/config/JwtProperties.java`
- `src/main/java/com/mspoverlay/global/config/CorsProperties.java`
- `src/main/java/com/mspoverlay/domain/auth/AuthService.java`
- `src/main/java/com/mspoverlay/domain/auth/AuthController.java`
- `src/main/java/com/mspoverlay/domain/auth/AuthTokenResponse.java`
- `src/main/java/com/mspoverlay/domain/auth/MeResponse.java`
- `src/main/java/com/mspoverlay/domain/auth/RefreshTokenRequest.java`
- `src/main/java/com/mspoverlay/infrastructure/oauth/OAuth2LoginSuccessHandler.java`

# Verification Result

- Google OAuth 클라이언트 값이 있으면 `/api/auth/google` 경로로 로그인 흐름이 시작되도록 확인
- 로그인 성공 후 사용자 저장/갱신, Access Token 발급, Refresh Token 저장이 동작하도록 구현된 것을 확인
- `/api/auth/me`, `/api/auth/refresh`, `/api/auth/logout`가 JWT/Refresh Token 흐름에 맞게 동작하도록 확인
- Testcontainers 기반 전체 테스트 통과 시 보안 설정과 애플리케이션 컨텍스트 로드가 함께 검증됨

# Decisions Made

- OAuth 클라이언트 정보가 없는 환경에서도 서버 부팅이 깨지지 않도록, Google 설정이 있을 때만 `oauth2Login()`을 활성화하도록 구성했다
- Access Token은 JWT로 무상태 검증하고, Refresh Token은 DB에 저장해 로그아웃 및 재발급 제어가 가능하도록 했다
- API 인증 실패 응답도 공통 에러 포맷을 따르도록 `RestAuthenticationEntryPoint`, `RestAccessDeniedHandler`를 추가했다

# Issues

- 로컬 개발 중 `.env` 자동 로드 오해와 DB 포트 충돌로 인증 흐름 확인이 지연됐다
- Google OAuth secret이 대화에 노출된 적이 있어 재발급/회전이 필요했다

# Next Steps

- 구현 순서 7단계인 overlay.json JSON Schema 검증기를 추가
- 이후 업로드 API에서 인증된 사용자 컨텍스트와 JWT 필터를 그대로 재사용
