# msp overlay Storage Specification

## 1. 저장 대상

- overlay.json
- thumbnail.png

## 2. 저장 방식

- JSON 원문은 파일로 저장
- DB에는 `json_path` 저장
- Thumbnail은 파일로 저장
- DB에는 `thumbnail_path` 저장

## 3. 권장 디렉터리 구조

```text
/storage/overlays/{overlayId}/overlay.json
/storage/overlays/{overlayId}/thumbnail.png
```

## 4. 업로드 방식

```http
POST /api/overlays
Content-Type: multipart/form-data
```

## 5. 업로드 흐름

```text
1. multipart/form-data 요청 수신
2. JWT 인증 확인
3. overlayJson 파일 확인
4. thumbnail 파일 확인
5. overlayJson Schema 검증
6. overlayId 기준 디렉터리 생성
7. overlay.json 저장
8. thumbnail.png 저장
9. DB에 json_path, thumbnail_path 저장
```

## 6. 수정 흐름

파일이 교체될 경우:

```text
1. 기존 파일 경로 조회
2. 새 파일 검증
3. 새 파일 저장
4. DB 경로 갱신
5. 기존 파일 삭제
```

## 7. 삭제 흐름

오버레이 삭제는 hard delete이다.

```text
1. 작성자 권한 확인
2. overlay 레코드 조회
3. user_libraries 매핑 삭제
4. overlays 레코드 삭제
5. overlay.json 즉시 삭제
6. thumbnail.png 즉시 삭제
7. 빈 디렉터리 삭제
```

## 8. 파일 보안

- overlayJson은 `.json`만 허용
- thumbnail은 `.png`만 허용
- 사용자가 보낸 원본 파일명을 서버 저장명으로 사용하지 않음
- 파일 크기 제한 적용
- 경로 traversal 방지
