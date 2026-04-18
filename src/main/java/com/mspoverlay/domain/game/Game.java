package com.mspoverlay.domain.game;

import com.mspoverlay.domain.platform.Platform;
import com.mspoverlay.global.util.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        name = "games",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_games_platform_id_slug", columnNames = {"platform_id", "slug"})
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Game extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "platform_id", nullable = false)
    private Platform platform;

    @Column(nullable = false, length = 100)
    private String slug;

    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    public Game(Platform platform, String slug, String displayName, boolean active) {
        this.platform = platform;
        this.slug = slug;
        this.displayName = displayName;
        this.active = active;
    }
}
