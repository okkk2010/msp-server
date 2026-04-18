# Task Summary

Java 24 기준으로 빌드 설정 호환성을 맞추기 위해 Spring Boot와 springdoc 버전을 상향 조정했다.

# Scope

- `build.gradle`의 Spring Boot 플러그인 버전 조정
- `build.gradle`의 springdoc 의존성 버전 조정
- Java 24 toolchain은 이미 반영되어 있어 유지
- Gradle Wrapper 추가나 실제 빌드 실행 환경 구성은 제외

# Changed Files

- `build.gradle`

# Verification Result

- `build.gradle`에서 Java toolchain이 `24`로 유지되는 것을 확인
- Spring Boot 플러그인을 `4.0.5`로 변경
- `springdoc-openapi-starter-webmvc-ui`를 `3.0.3`으로 변경
- 실제 빌드 검증은 현재 프로젝트에 `gradlew`가 없어 수행하지 못함

# Decisions Made

- Spring Boot 3.3.x는 공식 시스템 요구사항 기준 Java 23까지 호환이어서 Java 24 기준으로는 부적합하다고 판단했다
- Spring Boot 4.0.5는 공식 시스템 요구사항 기준 Java 26까지 호환이라 Java 24 기준으로 상향했다
- springdoc도 Spring Boot 4 계열에 맞춰 v3 계열로 올렸다

# Issues

- 현재 프로젝트에 Gradle Wrapper가 없어 IDE/로컬 Gradle 환경 차이로 다시 빌드 문제가 날 수 있다
- Java 24로 Gradle 자체를 실행하려면 Gradle 8.14 이상이 필요하다

# Next Steps

- Gradle Wrapper를 추가해 Gradle 버전을 고정
- IDE의 Gradle JVM이 Java 24 또는 최소한 Wrapper와 호환되는 버전인지 확인
