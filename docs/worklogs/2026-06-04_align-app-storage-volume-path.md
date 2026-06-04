# Task Summary

Aligned the app container storage volume mount path with the deployed `APP_STORAGE_BASE_PATH`.

# Scope

Only updated the app service storage volume target in `docker-compose.yml`.

# Changed Files

- `docker-compose.yml`
- `docs/worklogs/_index.md`
- `docs/worklogs/2026-06-04_align-app-storage-volume-path.md`

# Verification Result

- `docker compose config` succeeded and resolved `app_storage` to target `/storage`.
- Docker emitted a local config access warning for `C:\Users\okkk2\.docker\config.json`, but compose configuration was rendered successfully.

# Decisions Made

- Changed the app volume mount from `/app/storage` to `/storage` because the running production container uses `APP_STORAGE_BASE_PATH=/storage`.

# Issues

- Existing files that were written outside the named volume before this change may need to be copied into `app_storage` before recreating the container.

# Next Steps

- On the production host, back up any existing `/storage` files from the current container before recreating it.
- Recreate the app container so the changed volume target is applied.
