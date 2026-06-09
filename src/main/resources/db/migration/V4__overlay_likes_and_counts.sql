create table overlay_likes (
    id bigserial primary key,
    user_id bigint not null references users(id) on delete cascade,
    overlay_id bigint not null references overlays(id) on delete cascade,
    created_at timestamptz not null,
    unique (user_id, overlay_id)
);

create index idx_overlay_likes_user_id on overlay_likes(user_id);
create index idx_overlay_likes_overlay_id on overlay_likes(overlay_id);

alter table overlays add column like_count bigint not null default 0;
alter table overlays add column save_count bigint not null default 0;

-- Backfill aggregate save counts from existing library entries.
update overlays o
set save_count = coalesce((
    select count(*)
    from user_libraries ul
    where ul.overlay_id = o.id
), 0);

create index idx_overlays_like_count on overlays(like_count);
create index idx_overlays_save_count on overlays(save_count);
