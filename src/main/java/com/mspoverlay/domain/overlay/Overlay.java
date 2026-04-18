package com.mspoverlay.domain.overlay;

import java.math.BigDecimal;
import com.mspoverlay.domain.game.Game;
import com.mspoverlay.domain.platform.Platform;
import com.mspoverlay.domain.user.User;
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
        name = "overlays",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_overlays_overlay_id", columnNames = "overlay_id"),
                @UniqueConstraint(name = "uk_overlays_code", columnNames = "code")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Overlay extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "overlay_id", nullable = false, length = 100)
    private String overlayId;

    @Column(nullable = false, length = 6)
    private String code;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "platform_id", nullable = false)
    private Platform platform;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_user_id", nullable = false)
    private User authorUser;

    @Column(name = "schema_version", nullable = false, length = 20)
    private String schemaVersion;

    @Column(name = "canvas_base_width", nullable = false)
    private Integer canvasBaseWidth;

    @Column(name = "canvas_base_height", nullable = false)
    private Integer canvasBaseHeight;

    @Column(nullable = false, precision = 3, scale = 2)
    private BigDecimal opacity;

    @Column(name = "json_path", nullable = false, columnDefinition = "text")
    private String jsonPath;

    @Column(name = "thumbnail_path", nullable = false, columnDefinition = "text")
    private String thumbnailPath;

    public Overlay(
            String overlayId,
            String code,
            String name,
            String description,
            Platform platform,
            Game game,
            User authorUser,
            String schemaVersion,
            Integer canvasBaseWidth,
            Integer canvasBaseHeight,
            BigDecimal opacity,
            String jsonPath,
            String thumbnailPath
    ) {
        this.overlayId = overlayId;
        this.code = code;
        this.name = name;
        this.description = description;
        this.platform = platform;
        this.game = game;
        this.authorUser = authorUser;
        this.schemaVersion = schemaVersion;
        this.canvasBaseWidth = canvasBaseWidth;
        this.canvasBaseHeight = canvasBaseHeight;
        this.opacity = opacity;
        this.jsonPath = jsonPath;
        this.thumbnailPath = thumbnailPath;
    }

    public void updateGame(Game game) {
        this.game = game;
    }
}
