package com.mspoverlay.domain.overlay;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OverlayRepository extends JpaRepository<Overlay, Long> {

    Optional<Overlay> findByOverlayId(String overlayId);

    Optional<Overlay> findByCode(String code);

    boolean existsByCode(String code);
}
