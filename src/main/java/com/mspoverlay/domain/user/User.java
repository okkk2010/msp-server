package com.mspoverlay.domain.user;

import com.mspoverlay.global.util.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_users_oauth_provider_user_id", columnNames = {"oauth_provider", "oauth_provider_user_id"})
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "oauth_provider", nullable = false, length = 30)
    private OAuthProvider oauthProvider;

    @Column(name = "oauth_provider_user_id", nullable = false, length = 255)
    private String oauthProviderUserId;

    @Column(length = 255)
    private String email;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "profile_image_url", columnDefinition = "text")
    private String profileImageUrl;

    public User(OAuthProvider oauthProvider, String oauthProviderUserId, String email, String name, String profileImageUrl) {
        this.oauthProvider = oauthProvider;
        this.oauthProviderUserId = oauthProviderUserId;
        this.email = email;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
    }

    public void updateProfile(String email, String name, String profileImageUrl) {
        this.email = email;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
    }
}
