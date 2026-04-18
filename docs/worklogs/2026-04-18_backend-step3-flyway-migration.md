# Task Summary

문서의 구현 순서 3단계에 맞춰 Flyway DB 마이그레이션 구성을 추가했다.

# Scope

- Spring Boot 설정에 PostgreSQL 데이터소스와 Flyway 기본 설정 추가
- DB 명세 기준 초기 스키마 생성용 Flyway 마이그레이션 SQL 추가
- 4단계 이후 항목인 공통 응답, 도메인 엔티티, OAuth, API 구현은 제외

# Changed Files

- `src/main/resources/application.yml`
- `src/main/resources/db/migration/V1__init_schema.sql`

# Verification Result

- `application.yml`에 PostgreSQL 데이터소스와 `classpath:db/migration` 기반 Flyway 설정이 반영된 것을 확인
- `V1__init_schema.sql`에 DB 명세 기준 테이블, 인덱스, 플랫폼 시드 데이터가 반영된 것을 확인
- `docker compose up -d postgres` 검증은 Docker 엔진 파이프가 없어 실패
- 프로젝트에 `gradlew`가 없어 애플리케이션 부팅 기반 Flyway 실행 검증은 진행하지 못함

# Decisions Made

- JPA 자동 스키마 생성은 사용하지 않고 `ddl-auto: none`으로 고정했다
- 초기 스키마는 DB 명세 문서 기준 테이블, 인덱스, 플랫폼 시드 데이터까지 한 번에 구성했다
- 게임/오버레이 관련 후속 변경은 이후 버전 마이그레이션으로 분리할 수 있도록 V1 초기 스키마로 시작했다

# Issues

- Docker Desktop 엔진이 실행 중이 아니거나 접근 가능한 파이프가 없어 PostgreSQL 컨테이너를 실제 기동하지 못했다
- 현재 환경에는 Gradle Wrapper가 없어 애플리케이션 기동 기반의 Flyway 실행 검증이 바로 되지 않을 수 있다

# Next Steps

- Docker Desktop 실행 또는 Docker 엔진 연결 복구 후 PostgreSQL 컨테이너를 기동하고 Flyway 적용 여부를 검증
- 구현 순서 4단계인 공통 응답/예외 구조 구성을 진행
