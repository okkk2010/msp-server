# msp overlay Backend Dependency Specification

## 1. 필수 의존성

## Spring Boot

- Spring Web
- Spring Validation
- Spring Data JPA
- Spring Security
- OAuth2 Client

## Database

- PostgreSQL Driver
- Flyway

## JWT

- JJWT

사용 목적:

- Access Token 생성
- Refresh Token 생성
- JWT 검증
- 만료 시간 검증
- 사용자 claim 추출

## JSON Schema

- networknt json-schema-validator

사용 목적:

- overlay.json 구조 검증
- 필수 필드 검증
- opacity 범위 검증
- element type 검증

## API 문서

- springdoc-openapi

정책:

- local/dev 활성화
- prod 비활성화

## Test

- Spring Boot Test
- JUnit 5
- Testcontainers
- PostgreSQL Testcontainer

## 기타

- Lombok

## 2. Gradle 예시

```gradle
implementation 'org.springframework.boot:spring-boot-starter-web'
implementation 'org.springframework.boot:spring-boot-starter-validation'
implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
implementation 'org.springframework.boot:spring-boot-starter-security'
implementation 'org.springframework.boot:spring-boot-starter-oauth2-client'

runtimeOnly 'org.postgresql:postgresql'
implementation 'org.flywaydb:flyway-core'
implementation 'org.flywaydb:flyway-database-postgresql'

implementation 'io.jsonwebtoken:jjwt-api:{version}'
runtimeOnly 'io.jsonwebtoken:jjwt-impl:{version}'
runtimeOnly 'io.jsonwebtoken:jjwt-jackson:{version}'

implementation 'com.networknt:json-schema-validator:{version}'
implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:{version}'

compileOnly 'org.projectlombok:lombok'
annotationProcessor 'org.projectlombok:lombok'

testImplementation 'org.springframework.boot:spring-boot-starter-test'
testImplementation 'org.testcontainers:junit-jupiter'
testImplementation 'org.testcontainers:postgresql'
```

`{version}`은 프로젝트 생성 시점의 안정 버전을 사용한다.
