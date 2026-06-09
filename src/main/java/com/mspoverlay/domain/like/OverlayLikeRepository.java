package com.mspoverlay.domain.like;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OverlayLikeRepository extends JpaRepository<OverlayLike, Long> {

    boolean existsByUserIdAndOverlayId(Long userId, Long overlayId);

    Optional<OverlayLike> findByUserIdAndOverlayId(Long userId, Long overlayId);

    void deleteAllByOverlayId(Long overlayId);

    @Query("select l.overlay.id from OverlayLike l where l.user.id = :userId and l.overlay.id in :overlayIds")
    List<Long> findLikedOverlayIds(@Param("userId") Long userId, @Param("overlayIds") Collection<Long> overlayIds);
}
