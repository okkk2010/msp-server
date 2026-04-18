package com.mspoverlay.domain.platform;

import com.mspoverlay.global.util.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        name = "platforms",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_platforms_name", columnNames = "name"),
                @UniqueConstraint(name = "uk_platforms_slug", columnNames = "slug")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Platform extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 50)
    private String slug;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    public Platform(String name, String slug, boolean active) {
        this.name = name;
        this.slug = slug;
        this.active = active;
    }
}
