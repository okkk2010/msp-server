package com.mspoverlay.domain.like;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.mspoverlay.domain.overlay.Overlay;
import com.mspoverlay.domain.overlay.OverlayRepository;
import com.mspoverlay.domain.user.User;
import com.mspoverlay.domain.user.UserRepository;
import com.mspoverlay.global.exception.BusinessException;
import com.mspoverlay.global.exception.ErrorCode;

@Service
@Transactional(readOnly = true)
public class LikeService {

    private final OverlayLikeRepository overlayLikeRepository;
    private final OverlayRepository overlayRepository;
    private final UserRepository userRepository;

    public LikeService(
            OverlayLikeRepository overlayLikeRepository,
            OverlayRepository overlayRepository,
            UserRepository userRepository
    ) {
        this.overlayLikeRepository = overlayLikeRepository;
        this.overlayRepository = overlayRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void like(Long authenticatedUserId, Long overlayId) {
        User user = userRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Overlay overlay = overlayRepository.findById(overlayId)
                .orElseThrow(() -> new BusinessException(ErrorCode.OVERLAY_NOT_FOUND));

        if (overlayLikeRepository.existsByUserIdAndOverlayId(authenticatedUserId, overlayId)) {
            throw new BusinessException(ErrorCode.OVERLAY_ALREADY_LIKED);
        }

        overlayLikeRepository.save(new OverlayLike(user, overlay));
        overlay.increaseLikeCount();
    }

    @Transactional
    public void unlike(Long authenticatedUserId, Long overlayId) {
        OverlayLike overlayLike = overlayLikeRepository.findByUserIdAndOverlayId(authenticatedUserId, overlayId)
                .orElseThrow(() -> new BusinessException(ErrorCode.OVERLAY_LIKE_NOT_FOUND));

        overlayLikeRepository.delete(overlayLike);
        overlayLike.getOverlay().decreaseLikeCount();
    }
}
