아래 내용은 그대로 `docs/backend/overlay-code-load-spec.md` 같은 파일로 저장해서 Copilot/Codex 작업 지시서로 쓰면 됩니다.

````md
# Server 작업 명세서: Code 기반 Overlay 조회 API

## 1. 작업 목적

Windows Client에서 사용자가 6자리 Overlay Code만 입력해 서버에 저장된 오버레이 데이터를 가져올 수 있도록 Backend API를 구현한다.

이번 작업의 핵심 API는 다음이다.

```http
GET /api/overlays/code/{code}
````

이 API는 Windows Client의 Code Load 기능에서 직접 사용된다. Windows Client 명세에서도 Code 조회 API는 별도로 사용하고, 응답에는 `overlayJson`을 포함해야 한다고 확정되어 있다.

---

## 2. 구현 범위

### 포함 기능

* 6자리 code 기반 오버레이 단건 조회
* code 입력값 정규화

  * 앞뒤 공백 제거
  * 대문자 변환
* code 형식 검증
* DB에서 code로 overlay 조회
* overlay 메타데이터 반환
* overlayJson 원본 반환
* platform / game 정보 포함
* 공통 응답 포맷 적용
* 404 / 400 예외 처리
* Controller / Service / Repository / DTO 분리
* 단위 테스트 또는 통합 테스트 작성

### 제외 기능

* code 기반 overlay 생성
* code 재발급
* private overlay 권한 처리
* library 저장 처리
* 다운로드 통계 증가
* 추천 overlay 조회
* image/text element 처리
* Windows Client 캐시 처리

---

## 3. API 명세

## 3.1 Endpoint

```http
GET /api/overlays/code/{code}
```

## 3.2 인증

```text
불필요
```

이 API는 비로그인 Windows Client에서도 사용할 수 있어야 한다.

사용자는 웹에서 확인한 6자리 코드를 Windows Client에 입력하고, 서버에서 overlayJson을 받아 바로 적용한다.

## 3.3 Path Variable

| 이름   | 타입     | 필수 | 설명          |
| ---- | ------ | -- | ----------- |
| code | string | Y  | 6자리 오버레이 코드 |

## 3.4 code 규칙

```text
^[A-Z0-9]{6}$
```

### 처리 정책

```text
입력값 trim
→ uppercase 변환
→ 정규식 검증
→ DB 조회
```

### 예시

```text
abc123  → ABC123
 ABC123 → ABC123
m4n8q2  → M4N8Q2
```

---

## 4. 응답 명세

## 4.1 성공 응답

```json
{
  "success": true,
  "data": {
    "id": 1,
    "overlayId": "ovl_001",
    "code": "ABC123",
    "name": "Minecraft Center Focus",
    "description": "중앙 시야 고정용 오버레이",
    "platform": {
      "id": 1,
      "name": "Windows",
      "slug": "windows"
    },
    "game": {
      "id": 10,
      "slug": "minecraft",
      "displayName": "Minecraft"
    },
    "thumbnailUrl": "https://...",
    "schemaVersion": "1.0.0",
    "overlayJson": {
      "schemaVersion": "1.0.0",
      "overlayId": "ovl_001",
      "name": "Minecraft Center Focus",
      "platform": "windows",
      "game": {
        "id": "minecraft",
        "name": "Minecraft"
      },
      "canvas": {
        "baseWidth": 1920,
        "baseHeight": 1080
      },
      "overlaySettings": {
        "opacity": 0.85
      },
      "elements": [],
      "meta": {
        "createdAt": "2026-04-15T19:30:00+09:00",
        "updatedAt": "2026-04-15T19:45:00+09:00"
      }
    },
    "createdAt": "2026-04-15T10:00:00",
    "updatedAt": "2026-04-15T10:00:00"
  },
  "message": "ok"
}
```

## 4.2 응답 필수 조건

`overlayJson`은 반드시 포함한다.

Windows Client는 이 값을 받아 바로 JSON 파싱 후 OverlayWindow에 적용한다.

---

## 5. 실패 응답 명세

## 5.1 code 형식 오류

### 조건

```text
code가 6자리가 아님
영문 대문자 / 숫자 외 문자가 포함됨
```

### HTTP Status

```http
400 Bad Request
```

### 응답 예시

```json
{
  "success": false,
  "code": "INVALID_OVERLAY_CODE",
  "message": "overlay code must be 6 uppercase letters or digits"
}
```

---

## 5.2 overlay 없음

### 조건

```text
해당 code의 overlay가 DB에 없음
```

### HTTP Status

```http
404 Not Found
```

### 응답 예시

```json
{
  "success": false,
  "code": "OVERLAY_NOT_FOUND",
  "message": "overlay not found"
}
```

---

## 5.3 overlayJson 없음

### 조건

```text
DB row는 있으나 json_content 또는 json_path에서 JSON을 가져오지 못함
```

### HTTP Status

```http
500 Internal Server Error
```

### 응답 예시

```json
{
  "success": false,
  "code": "OVERLAY_JSON_NOT_FOUND",
  "message": "overlay json not found"
}
```

---

## 6. Backend 구조 기준

백엔드는 전체 시스템의 공통 데이터 허브이며, Web / Windows / Android Client가 공통으로 사용하는 REST API를 제공한다. 따라서 이번 API도 Windows 전용 Controller가 아니라 Overlay 도메인의 공통 조회 API로 구현한다.

추천 패키지 구조:

```text
com.mspoverlay
 ┣ domain
 ┃ ┗ overlay
 ┃   ┣ controller
 ┃   ┃ ┗ OverlayController.java
 ┃   ┣ service
 ┃   ┃ ┗ OverlayService.java
 ┃   ┣ repository
 ┃   ┃ ┗ OverlayRepository.java
 ┃   ┣ dto
 ┃   ┃ ┣ OverlayCodeResponse.java
 ┃   ┃ ┣ OverlayPlatformResponse.java
 ┃   ┃ ┗ OverlayGameResponse.java
 ┃   ┗ entity
 ┃     ┗ Overlay.java
 ┣ global
 ┃ ┣ exception
 ┃ ┣ response
 ┃ ┗ util
 ┗ MspOverlayApplication.java
```

---

## 7. Entity 기준

기존 `overlays` 테이블은 다음 필드를 가지고 있다고 가정한다.

```text
id
overlay_id
code
name
description
platform_id
game_id
author_user_id
schema_version
canvas_base_width
canvas_base_height
opacity
json_content
json_path
thumbnail_url
created_at
updated_at
```

`code`는 unique여야 한다.

```sql
code varchar(6) not null unique
```

---

## 8. Repository 명세

## 8.1 메서드

```java
Optional<Overlay> findByCode(String code);
```

## 8.2 조건

* code는 이미 Service에서 trim + uppercase + validation 처리된 값을 사용한다.
* Repository에서는 단순 조회만 담당한다.
* platform, game 정보가 필요하므로 N+1 문제가 생기지 않게 fetch join 또는 EntityGraph 적용을 고려한다.

예시:

```java
@EntityGraph(attributePaths = {"platform", "game"})
Optional<Overlay> findByCode(String code);
```

---

## 9. Service 명세

## 9.1 메서드

```java
OverlayCodeResponse getOverlayByCode(String code);
```

## 9.2 처리 흐름

```text
1. code 입력값 null 체크
2. trim 처리
3. uppercase 변환
4. 정규식 검증
5. overlayRepository.findByCode(normalizedCode)
6. 없으면 OVERLAY_NOT_FOUND
7. overlayJson 로드
   - json_content가 있으면 json_content 사용
   - json_path 방식이면 StorageService를 통해 파일 로드
8. overlayJson 파싱 가능 여부 확인
9. OverlayCodeResponse로 변환
10. 반환
```

## 9.3 code 검증 규칙

```java
private static final Pattern CODE_PATTERN = Pattern.compile("^[A-Z0-9]{6}$");
```

## 9.4 json_content / json_path 우선순위

MVP에서는 `json_content` 우선 사용을 권장한다.

```text
1순위: json_content
2순위: json_path
```

현재 MVP에서 파일 스토리지가 아직 안정화되지 않았다면 `json_content`만 사용해도 된다.
단, Entity에는 `json_path` 필드를 남겨 추후 파일 저장소 방식으로 확장할 수 있게 한다.

---

## 10. DTO 명세

## 10.1 OverlayCodeResponse

```java
public record OverlayCodeResponse(
    Long id,
    String overlayId,
    String code,
    String name,
    String description,
    OverlayPlatformResponse platform,
    OverlayGameResponse game,
    String thumbnailUrl,
    String schemaVersion,
    JsonNode overlayJson,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
```

## 10.2 OverlayPlatformResponse

```java
public record OverlayPlatformResponse(
    Long id,
    String name,
    String slug
) {}
```

## 10.3 OverlayGameResponse

```java
public record OverlayGameResponse(
    Long id,
    String slug,
    String displayName
) {}
```

## 10.4 overlayJson 타입

권장:

```java
JsonNode overlayJson
```

이유:

* 서버에서 JSON 원본 구조를 유지하기 좋음
* Windows Client가 그대로 사용할 수 있음
* JSON 내부 구조가 확장되어도 DTO 변경 부담이 적음

대안:

```java
Object overlayJson
Map<String, Object> overlayJson
String overlayJson
```

우선순위는 `JsonNode`를 권장한다.

---

## 11. Controller 명세

## 11.1 메서드

```java
@GetMapping("/code/{code}")
public ApiResponse<OverlayCodeResponse> getOverlayByCode(
    @PathVariable String code
) {
    return ApiResponse.ok(overlayService.getOverlayByCode(code));
}
```

## 11.2 Controller 경로

```java
@RestController
@RequestMapping("/api/overlays")
@RequiredArgsConstructor
public class OverlayController {
    private final OverlayService overlayService;
}
```

최종 API:

```http
GET /api/overlays/code/{code}
```

---

## 12. 공통 응답 포맷

기존 백엔드 명세의 공통 응답 포맷을 따른다.

```json
{
  "success": true,
  "data": {},
  "message": "ok"
}
```

에러 응답:

```json
{
  "success": false,
  "code": "ERROR_CODE",
  "message": "error message"
}
```

---

## 13. 예외 코드 추가

다음 예외 코드를 추가한다.

```text
INVALID_OVERLAY_CODE
OVERLAY_NOT_FOUND
OVERLAY_JSON_NOT_FOUND
INVALID_OVERLAY_JSON
```

## 13.1 INVALID_OVERLAY_CODE

```text
HTTP 400
code 형식이 잘못된 경우
```

## 13.2 OVERLAY_NOT_FOUND

```text
HTTP 404
code에 해당하는 overlay가 없는 경우
```

## 13.3 OVERLAY_JSON_NOT_FOUND

```text
HTTP 500
overlay row는 있으나 JSON 원본을 찾을 수 없는 경우
```

## 13.4 INVALID_OVERLAY_JSON

```text
HTTP 500 또는 422
저장된 overlayJson이 서버에서 파싱 불가능한 경우
```

MVP에서는 업로드 시 검증을 전제로 하므로 조회 단계에서는 500 처리해도 된다.

---

## 14. 보안 정책

## 14.1 인증 제외

이 API는 인증 없이 호출 가능하다.

Spring Security 사용 시 다음 경로를 permitAll 처리한다.

```text
GET /api/overlays/code/**
```

## 14.2 반환 제한

현재 MVP에서는 code를 아는 사용자는 overlayJson을 가져갈 수 있다.

추후 공개/비공개 기능이 추가되면 다음 정책을 적용한다.

```text
public overlay:
  비로그인 조회 가능

private overlay:
  작성자 또는 library 저장 사용자만 조회 가능
```

현재 MVP에서는 visibility 필드가 명세에 없으므로 적용하지 않는다.

---

## 15. JSON 처리 정책

Overlay JSON은 현재 MVP 기준 다음 구조를 가진다.

```text
schemaVersion
overlayId
name
platform
game
canvas
overlaySettings
elements
meta
```

지원 element는 다음만 허용한다.

```text
rect
circle
line
```

제외 element:

```text
image
text
```

조회 API는 저장된 JSON을 수정하지 않고 그대로 반환한다.
단, 서버 내부 파싱 확인은 수행할 수 있다.

---

## 16. 테스트 명세

## 16.1 정상 조회

### 조건

```text
DB에 code = ABC123 overlay 존재
json_content에 유효한 overlayJson 존재
```

### 요청

```http
GET /api/overlays/code/ABC123
```

### 기대 결과

```text
200 OK
success = true
data.code = ABC123
data.overlayJson 존재
data.overlayJson.overlayId 존재
```

---

## 16.2 소문자 code 조회

### 요청

```http
GET /api/overlays/code/abc123
```

### 기대 결과

```text
200 OK
서버 내부에서 ABC123으로 변환 후 조회
data.code = ABC123
```

---

## 16.3 공백 포함 code 조회

PathVariable에서는 공백 입력 가능성이 낮지만, URL 인코딩으로 들어올 수 있으므로 Service에서 trim 처리한다.

### 요청

```http
GET /api/overlays/code/%20ABC123%20
```

### 기대 결과

```text
200 OK
data.code = ABC123
```

---

## 16.4 잘못된 code 형식

### 요청

```http
GET /api/overlays/code/ABC12
```

### 기대 결과

```text
400 Bad Request
code = INVALID_OVERLAY_CODE
```

---

## 16.5 허용되지 않는 문자

### 요청

```http
GET /api/overlays/code/ABC-12
```

### 기대 결과

```text
400 Bad Request
code = INVALID_OVERLAY_CODE
```

---

## 16.6 존재하지 않는 code

### 요청

```http
GET /api/overlays/code/ZZ9999
```

### 기대 결과

```text
404 Not Found
code = OVERLAY_NOT_FOUND
```

---

## 16.7 overlayJson 없음

### 조건

```text
overlay row는 존재
json_content = null
json_path = null
```

### 기대 결과

```text
500 Internal Server Error
code = OVERLAY_JSON_NOT_FOUND
```

---

## 17. Swagger 문서화

Swagger/OpenAPI에 다음 내용을 표시한다.

```text
GET /api/overlays/code/{code}
요약: 6자리 code로 overlay 단건 조회
설명: Windows Client에서 code 입력만으로 overlayJson을 가져오기 위한 API
인증: 불필요
성공 응답: OverlayCodeResponse
오류 응답:
- 400 INVALID_OVERLAY_CODE
- 404 OVERLAY_NOT_FOUND
- 500 OVERLAY_JSON_NOT_FOUND
```

---

## 18. 구현 순서

```text
1. OverlayRepository에 findByCode 추가
2. OverlayCodeResponse DTO 추가
3. OverlayPlatformResponse / OverlayGameResponse DTO 확인 또는 추가
4. OverlayService에 getOverlayByCode 구현
5. code 정규화 유틸 또는 private 메서드 구현
6. overlayJson 로드 로직 구현
7. OverlayController에 GET /code/{code} 추가
8. 예외 코드 추가
9. SecurityConfig permitAll 경로 추가
10. Swagger 설명 추가
11. 테스트 작성
12. Postman으로 수동 검증
```

---

## 19. 완료 기준

이번 작업은 아래 조건을 만족하면 완료로 본다.

```text
1. GET /api/overlays/code/{code} API가 존재한다.
2. 인증 없이 호출할 수 있다.
3. code는 trim + uppercase 처리된다.
4. code 형식이 잘못되면 400을 반환한다.
5. code에 해당하는 overlay가 없으면 404를 반환한다.
6. 정상 code 조회 시 overlay 메타데이터를 반환한다.
7. 정상 code 조회 시 overlayJson을 반드시 반환한다.
8. overlayJson은 Windows Client가 바로 파싱 가능한 구조다.
9. 응답에는 platform, game 정보가 포함된다.
10. 공통 응답 포맷을 따른다.
11. Swagger 또는 API 문서에서 확인 가능하다.
12. 테스트 또는 Postman 검증이 완료된다.
```

```

핵심만 다시 보면, 서버 쪽 작업은 **기존 `GET /api/overlays/{id}` 상세 조회와 별개로 `GET /api/overlays/code/{code}` 전용 조회 API를 추가하는 것**입니다. Windows Client 명세에서 이미 Code 조회 API는 별도 사용, 응답에는 `overlayJson` 포함으로 확정되어 있고 :contentReference[oaicite:0]{index=0}, 전체 시스템도 Backend가 오버레이 JSON 원본을 검증·관리하고 공통 REST API를 제공하는 구조로 잡혀 있습니다. :contentReference[oaicite:1]{index=1}

또한 현재 Overlay JSON MVP는 `schemaVersion`, `overlayId`, `name`, `platform`, `game`, `canvas`, `overlaySettings`, `elements`, `meta`를 중심으로 하고, 지원 요소는 `rect`, `circle`, `line`만 가져가는 구조라서 이 API도 해당 JSON을 그대로 반환하는 방향이 맞습니다. :contentReference[oaicite:2]{index=2}
```
