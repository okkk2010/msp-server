insert into games (platform_id, slug, display_name, is_active, created_at, updated_at)
select p.id, g.slug, g.display_name, true, now(), now()
from platforms p
cross join (
    values
        ('valorant', 'VALORANT'),
        ('minecraft', 'Minecraft'),
        ('overwatch-2', 'Overwatch 2'),
        ('lethal-company', 'Lethal Company'),
        ('repo', 'R.E.P.O.'),
        ('apex-legends', 'Apex Legends'),
        ('fortnite', 'Fortnite'),
        ('pubg-battlegrounds', 'PUBG: Battlegrounds'),
        ('rainbow-six-siege', 'Tom Clancy''s Rainbow Six Siege'),
        ('rust', 'Rust'),
        ('dead-by-daylight', 'Dead by Daylight'),
        ('destiny-2', 'Destiny 2'),
        ('the-finals', 'THE FINALS'),
        ('palworld', 'Palworld'),
        ('grand-theft-auto-v', 'Grand Theft Auto V')
) as g(slug, display_name)
where p.slug = 'windows'
on conflict (platform_id, slug) do update
set display_name = excluded.display_name,
    is_active = excluded.is_active,
    updated_at = now();
