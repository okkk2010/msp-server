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

create table refresh_tokens (
    id bigserial primary key,
    user_id bigint not null references users(id) on delete cascade,
    token varchar(512) not null unique,
    expires_at timestamptz not null,
    created_at timestamptz not null
);

create table platforms (
    id bigserial primary key,
    name varchar(50) not null unique,
    slug varchar(50) not null unique,
    is_active boolean not null default true,
    created_at timestamptz not null,
    updated_at timestamptz not null
);

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

create table user_libraries (
    id bigserial primary key,
    user_id bigint not null references users(id) on delete cascade,
    overlay_id bigint not null references overlays(id) on delete cascade,
    created_at timestamptz not null,
    unique (user_id, overlay_id)
);

create index idx_overlays_platform_id on overlays(platform_id);
create index idx_overlays_game_id on overlays(game_id);
create index idx_overlays_author_user_id on overlays(author_user_id);
create index idx_overlays_code on overlays(code);
create index idx_user_libraries_user_id on user_libraries(user_id);
create index idx_refresh_tokens_user_id on refresh_tokens(user_id);

insert into platforms (name, slug, is_active, created_at, updated_at)
values
('Windows', 'windows', true, now(), now()),
('Android', 'android', true, now(), now());

