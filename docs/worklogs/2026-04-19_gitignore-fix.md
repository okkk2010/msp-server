# Task Summary

`.gitignore`가 기대대로 동작하지 않는 원인을 확인하고, 누락된 ignore 규칙과 이미 추적 중이던 산출물을 정리했다.

# Scope

- `.gitignore` 규칙 점검 및 최소 보완
- 이미 Git이 추적 중이던 IDE 설정/빌드 산출물의 인덱스 제외
- 기능 코드나 설정 로직 변경은 제외

# Changed Files

- `.gitignore`
- `docs/worklogs/2026-04-19_gitignore-fix.md`
- `docs/worklogs/_index.md`

# Verification Result

- `git check-ignore -v .gradle-user .vscode/settings.json src/main/resources/application-local.yml`로 ignore 규칙 적용 확인
- `git status --short --branch`로 `.gitignore` 수정과 기존 추적 파일의 인덱스 삭제 상태 확인

# Decisions Made

- 누락된 `.gradle-user/`, `.vscode/`를 `.gitignore`에 추가했다
- 이미 추적 중이던 `bin/`, `.vscode/settings.json`은 `git rm --cached`로 인덱스에서만 제거했다
- `application-local.yml`은 기존 규칙이 이미 정상 적용 중이어서 수정하지 않았다

# Issues

- 없음

# Next Steps

- 필요하면 다른 로컬 전용 산출물도 같은 방식으로 ignore 규칙에 포함
- 커밋 시 `bin/` 및 `.vscode/settings.json` 삭제가 함께 반영되는지 확인
