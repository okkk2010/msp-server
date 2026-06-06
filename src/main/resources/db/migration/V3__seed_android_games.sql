insert into games (platform_id, slug, display_name, is_active, created_at, updated_at)
select p.id, g.slug, g.display_name, true, now(), now()
from platforms p
cross join (
    values
        ('minecraft', 'Minecraft'),
        ('roblox', 'Roblox'),
        ('genshin-impact', 'Genshin Impact'),
        ('honkai-star-rail', 'Honkai: Star Rail'),
        ('wuthering-waves', 'Wuthering Waves'),
        ('pubg-mobile', 'PUBG Mobile'),
        ('call-of-duty-mobile', 'Call of Duty: Mobile'),
        ('fortnite', 'Fortnite'),
        ('brawl-stars', 'Brawl Stars'),
        ('clash-royale', 'Clash Royale'),
        ('clash-of-clans', 'Clash of Clans'),
        ('pokemon-go', 'Pokemon GO'),
        ('league-of-legends-wild-rift', 'League of Legends: Wild Rift'),
        ('mobile-legends-bang-bang', 'Mobile Legends: Bang Bang'),
        ('among-us', 'Among Us')
) as g(slug, display_name)
where p.slug = 'android'
on conflict (platform_id, slug) do update
set display_name = excluded.display_name,
    is_active = excluded.is_active,
    updated_at = now();
