package com.mspoverlay.domain.auth;

import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.mspoverlay.domain.user.OAuthProvider;
import com.mspoverlay.domain.user.User;
import com.mspoverlay.domain.user.UserRepository;
import com.mspoverlay.global.exception.BusinessException;
import com.mspoverlay.global.exception.ErrorCode;
import com.mspoverlay.global.security.AuthenticatedUser;
import com.mspoverlay.global.security.JwtTokenProvider;

@Service
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            JwtTokenProvider jwtTokenProvider
    ) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional
    public AuthTokenResponse login(
            OAuthProvider provider,
            String providerUserId,
            String email,
            String name,
            String profileImageUrl
    ) {
        User user = userRepository.findByOauthProviderAndOauthProviderUserId(provider, providerUserId)
                .map(existingUser -> {
                    existingUser.updateProfile(email, name, profileImageUrl);
                    return existingUser;
                })
                .orElseGet(() -> userRepository.save(new User(provider, providerUserId, email, name, profileImageUrl)));

        return issueTokens(user);
    }

    @Transactional
    public AuthTokenResponse refresh(String refreshToken) {
        RefreshToken savedRefreshToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new BusinessException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        if (savedRefreshToken.isExpired(OffsetDateTime.now())) {
            refreshTokenRepository.delete(savedRefreshToken);
            throw new BusinessException(ErrorCode.TOKEN_EXPIRED);
        }

        User user = savedRefreshToken.getUser();
        String accessToken = jwtTokenProvider.createAccessToken(toAuthenticatedUser(user));
        return AuthTokenResponse.of(
                accessToken,
                savedRefreshToken.getToken(),
                savedRefreshToken.getExpiresAt()
        );
    }

    @Transactional
    public void logout(Long userId) {
        refreshTokenRepository.deleteAllByUserId(userId);
    }

    public MeResponse getCurrentUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return MeResponse.from(user);
    }

    private AuthTokenResponse issueTokens(User user) {
        AuthenticatedUser authenticatedUser = toAuthenticatedUser(user);
        String accessToken = jwtTokenProvider.createAccessToken(authenticatedUser);
        OffsetDateTime refreshTokenExpiresAt = jwtTokenProvider.getRefreshTokenExpiry();
        String refreshTokenValue = UUID.randomUUID().toString();

        refreshTokenRepository.deleteAllByUserId(user.getId());
        refreshTokenRepository.save(new RefreshToken(user, refreshTokenValue, refreshTokenExpiresAt));

        return AuthTokenResponse.of(accessToken, refreshTokenValue, refreshTokenExpiresAt);
    }

    private AuthenticatedUser toAuthenticatedUser(User user) {
        return new AuthenticatedUser(user.getId(), user.getEmail(), user.getName());
    }
}
