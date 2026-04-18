# msp overlay JSON Schema Specification

## 1. 문서 목적

이 문서는 `overlay.json` 구조와 서버 검증 기준을 정의한다.

## 2. 저장 단위

```text
overlay.json
thumbnail.png
```

## 3. 최상위 구조

```json
{
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
}
```

## 4. 필수 필드

- schemaVersion
- overlayId
- name
- platform
- canvas
- overlaySettings
- elements
- meta

`game`은 필수값이 아니며 등록 후 수정 가능하다.

## 5. platform

허용값:

```text
windows
android
```

## 6. canvas

- baseWidth: number, 필수
- baseHeight: number, 필수

## 7. overlaySettings

- opacity: number, 필수, `0.0 ~ 1.0`

저장 제외 항목:

- visible
- clickThrough
- followTargetWindow
- showOnlyWhenTargetFocused
- alwaysOnTop
- anchorPreset
- offsetX
- offsetY

## 8. elements

허용 타입:

- rect
- circle
- line

제외 타입:

- image
- text

## 9. rect 필드

- id
- type: rect
- x
- y
- width
- height
- rotation
- opacity
- zIndex
- visible
- locked
- fillColor
- strokeColor
- strokeWidth
- cornerRadius

## 10. circle 필드

- id
- type: circle
- x
- y
- width
- height
- rotation
- opacity
- zIndex
- visible
- locked
- fillColor
- strokeColor
- strokeWidth

## 11. line 필드

- id
- type: line
- x1
- y1
- x2
- y2
- opacity
- zIndex
- visible
- locked
- strokeColor
- strokeWidth
- dashStyle: solid/dash/dot

## 12. meta

- createdAt: ISO-8601 string
- updatedAt: ISO-8601 string

## 13. 서버 검증

사용 라이브러리:

- networknt json-schema-validator

검증 시점:

- 오버레이 업로드
- overlayJson을 변경하는 오버레이 수정

검증 실패:

- HTTP 400
- `INVALID_OVERLAY_JSON`
