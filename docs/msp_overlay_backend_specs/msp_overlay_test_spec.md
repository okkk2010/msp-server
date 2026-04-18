# msp overlay Backend Test Specification

## 1. 테스트 도구

- JUnit 5
- Spring Boot Test
- Testcontainers
- PostgreSQL Testcontainer
- MockMvc 또는 RestAssured
- Postman

## 2. 단위 테스트

- JWT 생성/검증
- Refresh Token 만료 검증
- Overlay JSON Schema 검증
- code 중복 검사
- 권한 검사
- 파일 경로 생성 로직

## 3. 통합 테스트

Testcontainers 기반 PostgreSQL을 사용한다.

- User 저장/조회
- RefreshToken 저장/삭제
- Platform 조회
- Game 조회
- Overlay 업로드
- Overlay 수정
- Overlay 삭제
- Library 저장
- Library 조회
- Library 삭제

## 4. 주요 시나리오

### 오버레이 업로드 성공

multipart/form-data로 overlayJson과 thumbnail을 업로드하면 DB에 overlay 레코드가 저장되고, json_path와 thumbnail_path가 저장되며, 파일이 실제 경로에 저장된다.

### JSON 검증 실패

필수 필드가 누락된 overlayJson을 업로드하면 `400 INVALID_OVERLAY_JSON`을 반환한다.

### 오버레이 수정 성공

작성자가 overlayJson 또는 thumbnail을 수정하면 새 파일 저장, DB 경로 갱신, 기존 파일 삭제가 수행된다.

### 오버레이 삭제 성공

작성자가 삭제 요청을 보내면 overlay DB 레코드가 hard delete되고, user_libraries 매핑과 overlay.json, thumbnail.png가 삭제된다.

### 권한 실패

작성자가 아닌 사용자가 수정/삭제 요청을 보내면 `403 FORBIDDEN`을 반환한다.

### Refresh Token 재발급

DB에 유효한 Refresh Token이 있으면 Access Token을 재발급한다.

### 로그아웃

로그아웃 시 DB에서 Refresh Token이 삭제된다.
