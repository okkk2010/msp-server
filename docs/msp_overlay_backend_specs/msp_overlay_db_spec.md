# msp overlay DB Specification

## 1. 공통 정책

- DBMS: PostgreSQL
- 시간 타입: `timestamptz`
- 마이그레이션: Flyway 사용

## 2. users

```sql
create table users (
    id bigserial primary key,
    oauth_provider varchar(30) not null,
    oauth_provider_user_id varchar(255) not null,
    email varchar(255),
    name varchar(100) not null,
    profile_image_url text,
    created_at timestamptz not null,
    updated_at timestamptz not null,
    unique (oauth_provider, oauth_provider_user_id)
);
```

## 3. refresh_tokens

```sql
create table refresh_tokens (
    id bigserial primary key,
    user_id bigint not null references users(id) on delete cascade,
    token varchar(512) not null unique,
    expires_at timestamptz not null,
    created_at timestamptz not null
);
```

정책:

- Access Token: 30분
- Refresh Token: 14일
- 로그아웃 시 Refresh Token 삭제
- MVP에서는 원문 저장
- 추후 해시 저장 검토

## 4. platforms

```sql
create table platforms (
    id bigserial primary key,
    name varchar(50) not null unique,
    slug varchar(50) not null unique,
    is_active boolean not null default true,
    created_at timestamptz not null,
    updated_at timestamptz not null
);
```

초기 데이터:

```sql
insert into platforms (name, slug, is_active, created_at, updated_at)
values
('Windows', 'windows', true, now(), now()),
('Android', 'android', true, now(), now());
```

## 5. games

```sql
create table games (
    id bigserial primary key,
    platform_id bigint not null references platforms(id),
    slug varchar(100) not null,
    display_name varchar(100) not null,
    is_active boolean not null default true,
    created_at timestamptz not null,
    updated_at timestamptz not null,
    unique (platform_id, slug)
);
```

정책:

- 오버레이 등록 시 게임 정보는 필수 아님
- 등록 후 수정 가능

## 6. overlays

```sql
create table overlays (
    id bigserial primary key,
    overlay_id varchar(100) not null unique,
    code varchar(6) not null unique,
    name varchar(150) not null,
    description text,
    platform_id bigint not null references platforms(id),
    game_id bigint references games(id),
    author_user_id bigint not null references users(id),
    schema_version varchar(20) not null,
    canvas_base_width int not null,
    canvas_base_height int not null,
    opacity numeric(3,2) not null,
    json_path text not null,
    thumbnail_path text not null,
    created_at timestamptz not null,
    updated_at timestamptz not null
);
```

정책:

- JSON 원문은 DB에 저장하지 않음
- DB에는 `json_path` 저장
- 삭제는 hard delete
- 삭제 시 파일 즉시 삭제

## 7. user_libraries

```sql
create table user_libraries (
    id bigserial primary key,
    user_id bigint not null references users(id) on delete cascade,
    overlay_id bigint not null references overlays(id) on delete cascade,
    created_at timestamptz not null,
    unique (user_id, overlay_id)
);
```

## 8. 권장 인덱스

```sql
create index idx_overlays_platform_id on overlays(platform_id);
create index idx_overlays_game_id on overlays(game_id);
create index idx_overlays_author_user_id on overlays(author_user_id);
create index idx_overlays_code on overlays(code);
create index idx_user_libraries_user_id on user_libraries(user_id);
create index idx_refresh_tokens_user_id on refresh_tokens(user_id);
```
