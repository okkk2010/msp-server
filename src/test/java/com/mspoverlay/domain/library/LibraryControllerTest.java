package com.mspoverlay.domain.library;

import com.mspoverlay.global.response.ApiResponse;
import com.mspoverlay.global.security.AuthenticatedUser;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LibraryControllerTest {

    @Test
    void getLibrary_returnsApiResponse() {
        LibraryService libraryService = mock(LibraryService.class);
        LibraryController controller = new LibraryController(libraryService);
        when(libraryService.getLibrary(1L)).thenReturn(List.of(
                new LibraryItemResponse(
                        10L,
                        OffsetDateTime.parse("2026-04-19T12:00:00+09:00"),
                        null
                )
        ));

        ApiResponse<List<LibraryItemResponse>> response = controller.getLibrary(new AuthenticatedUser(1L, "user@example.com", "User"));

        assertThat(response.success()).isTrue();
        assertThat(response.data()).hasSize(1);
    }

    @Test
    void save_delegatesToService() {
        LibraryService libraryService = mock(LibraryService.class);
        LibraryController controller = new LibraryController(libraryService);

        ApiResponse<Void> response = controller.save(
                new AuthenticatedUser(1L, "user@example.com", "User"),
                new LibrarySaveRequest(5L)
        );

        assertThat(response.success()).isTrue();
        verify(libraryService).save(1L, 5L);
    }

    @Test
    void delete_delegatesToService() {
        LibraryService libraryService = mock(LibraryService.class);
        LibraryController controller = new LibraryController(libraryService);

        ApiResponse<Void> response = controller.delete(new AuthenticatedUser(1L, "user@example.com", "User"), 5L);

        assertThat(response.success()).isTrue();
        verify(libraryService).delete(1L, 5L);
    }
}
