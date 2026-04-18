package com.mspoverlay.domain.library;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLibraryRepository extends JpaRepository<UserLibrary, Long> {

    List<UserLibrary> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    boolean existsByUserIdAndOverlayId(Long userId, Long overlayId);

    void deleteByUserIdAndOverlayId(Long userId, Long overlayId);
}
