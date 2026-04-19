package com.mspoverlay.domain.library;

import java.util.List;
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
public class LibraryService {

    private final UserLibraryRepository userLibraryRepository;
    private final OverlayRepository overlayRepository;
    private final UserRepository userRepository;

    public LibraryService(
            UserLibraryRepository userLibraryRepository,
            OverlayRepository overlayRepository,
            UserRepository userRepository
    ) {
        this.userLibraryRepository = userLibraryRepository;
        this.overlayRepository = overlayRepository;
        this.userRepository = userRepository;
    }

    public List<LibraryItemResponse> getLibrary(Long authenticatedUserId) {
        return userLibraryRepository.findAllWithOverlayByUserIdOrderByCreatedAtDesc(authenticatedUserId)
                .stream()
                .map(LibraryItemResponse::from)
                .toList();
    }

    @Transactional
    public void save(Long authenticatedUserId, Long overlayId) {
        User user = userRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Overlay overlay = overlayRepository.findById(overlayId)
                .orElseThrow(() -> new BusinessException(ErrorCode.OVERLAY_NOT_FOUND));

        if (userLibraryRepository.existsByUserIdAndOverlayId(authenticatedUserId, overlayId)) {
            throw new BusinessException(ErrorCode.LIBRARY_ALREADY_SAVED);
        }

        userLibraryRepository.save(new UserLibrary(user, overlay));
    }

    @Transactional
    public void delete(Long authenticatedUserId, Long overlayId) {
        UserLibrary userLibrary = userLibraryRepository.findByUserIdAndOverlayId(authenticatedUserId, overlayId)
                .orElseThrow(() -> new BusinessException(ErrorCode.LIBRARY_NOT_FOUND));
        userLibraryRepository.delete(userLibrary);
    }
}
