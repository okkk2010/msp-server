package com.mspoverlay.domain.library;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLibraryRepository extends JpaRepository<UserLibrary, Long> {

    List<UserLibrary> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    @EntityGraph(attributePaths = {"overlay", "overlay.platform", "overlay.game", "overlay.authorUser"})
    List<UserLibrary> findAllWithOverlayByUserIdOrderByCreatedAtDesc(Long userId);

    boolean existsByUserIdAndOverlayId(Long userId, Long overlayId);

    void deleteByUserIdAndOverlayId(Long userId, Long overlayId);

    void deleteAllByOverlayId(Long overlayId);

    Optional<UserLibrary> findByUserIdAndOverlayId(Long userId, Long overlayId);
}
