package com.mspoverlay.domain.library;

import com.mspoverlay.domain.overlay.Overlay;
import com.mspoverlay.domain.user.User;
import com.mspoverlay.global.util.BaseCreatedEntity;
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
        name = "user_libraries",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_libraries_user_id_overlay_id", columnNames = {"user_id", "overlay_id"})
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserLibrary extends BaseCreatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "overlay_id", nullable = false)
    private Overlay overlay;

    public UserLibrary(User user, Overlay overlay) {
        this.user = user;
        this.overlay = overlay;
    }
}
