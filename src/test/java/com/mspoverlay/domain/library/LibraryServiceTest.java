package com.mspoverlay.domain.library;

import com.mspoverlay.domain.overlay.Overlay;
import com.mspoverlay.domain.overlay.OverlayRepository;
import com.mspoverlay.domain.platform.Platform;
import com.mspoverlay.domain.user.OAuthProvider;
import com.mspoverlay.domain.user.User;
import com.mspoverlay.domain.user.UserRepository;
import com.mspoverlay.global.exception.BusinessException;
import com.mspoverlay.global.exception.ErrorCode;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LibraryServiceTest {

    @Test
    void getLibrary_returnsSavedItems() {
        UserLibraryRepository userLibraryRepository = mock(UserLibraryRepository.class);
        OverlayRepository overlayRepository = mock(OverlayRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        LibraryService service = new LibraryService(userLibraryRepository, overlayRepository, userRepository);

        when(userLibraryRepository.findAllWithOverlayByUserIdOrderByCreatedAtDesc(1L))
                .thenReturn(List.of(library(10L, 5L)));

        List<LibraryItemResponse> responses = service.getLibrary(1L);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).libraryId()).isEqualTo(10L);
        assertThat(responses.get(0).overlay().overlayId()).isEqualTo("ovl_001");
    }

    @Test
    void save_rejectsAlreadySavedOverlay() {
        UserLibraryRepository userLibraryRepository = mock(UserLibraryRepository.class);
        OverlayRepository overlayRepository = mock(OverlayRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        LibraryService service = new LibraryService(userLibraryRepository, overlayRepository, userRepository);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user(1L)));
        when(overlayRepository.findById(5L)).thenReturn(Optional.of(overlay(5L)));
        when(userLibraryRepository.existsByUserIdAndOverlayId(1L, 5L)).thenReturn(true);

        assertThatThrownBy(() -> service.save(1L, 5L))
                .isInstanceOf(BusinessException.class)
                .extracting(exception -> ((BusinessException) exception).getErrorCode())
                .isEqualTo(ErrorCode.LIBRARY_ALREADY_SAVED);

        verify(userLibraryRepository, never()).save(any());
    }

    @Test
    void save_storesOverlayInLibrary() {
        UserLibraryRepository userLibraryRepository = mock(UserLibraryRepository.class);
        OverlayRepository overlayRepository = mock(OverlayRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        LibraryService service = new LibraryService(userLibraryRepository, overlayRepository, userRepository);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user(1L)));
        when(overlayRepository.findById(5L)).thenReturn(Optional.of(overlay(5L)));
        when(userLibraryRepository.existsByUserIdAndOverlayId(1L, 5L)).thenReturn(false);

        service.save(1L, 5L);

        verify(userLibraryRepository).save(any(UserLibrary.class));
    }

    @Test
    void delete_rejectsMissingLibraryItem() {
        UserLibraryRepository userLibraryRepository = mock(UserLibraryRepository.class);
        OverlayRepository overlayRepository = mock(OverlayRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        LibraryService service = new LibraryService(userLibraryRepository, overlayRepository, userRepository);

        when(userLibraryRepository.findByUserIdAndOverlayId(1L, 5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(1L, 5L))
                .isInstanceOf(BusinessException.class)
                .extracting(exception -> ((BusinessException) exception).getErrorCode())
                .isEqualTo(ErrorCode.LIBRARY_NOT_FOUND);
    }

    @Test
    void delete_removesSavedOverlay() {
        UserLibraryRepository userLibraryRepository = mock(UserLibraryRepository.class);
        OverlayRepository overlayRepository = mock(OverlayRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        LibraryService service = new LibraryService(userLibraryRepository, overlayRepository, userRepository);
        UserLibrary library = library(10L, 5L);

        when(userLibraryRepository.findByUserIdAndOverlayId(1L, 5L)).thenReturn(Optional.of(library));

        service.delete(1L, 5L);

        verify(userLibraryRepository).delete(library);
    }

    private User user(Long id) {
        User user = new User(OAuthProvider.GOOGLE, "google-user-" + id, "user@example.com", "User", null);
        setId(user, User.class, id);
        return user;
    }

    private Overlay overlay(Long id) {
        Platform platform = new Platform("Windows", "windows", true);
        setId(platform, Platform.class, 1L);
        Overlay overlay = new Overlay(
                "ovl_001",
                "ABC123",
                "Overlay",
                "desc",
                platform,
                null,
                user(1L),
                "1.0.0",
                1920,
                1080,
                new BigDecimal("0.85"),
                "/storage/overlays/ovl_001/overlay.json",
                "/storage/overlays/ovl_001/thumbnail.png"
        );
        setId(overlay, Overlay.class, id);
        return overlay;
    }

    private UserLibrary library(Long libraryId, Long overlayId) {
        UserLibrary library = new UserLibrary(user(1L), overlay(overlayId));
        setId(library, UserLibrary.class, libraryId);
        return library;
    }

    private void setId(Object target, Class<?> type, Long id) {
        try {
            var field = type.getDeclaredField("id");
            field.setAccessible(true);
            field.set(target, id);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException(exception);
        }
    }
}
