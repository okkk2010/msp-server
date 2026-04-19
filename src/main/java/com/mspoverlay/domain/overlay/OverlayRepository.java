package com.mspoverlay.domain.overlay;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OverlayRepository extends JpaRepository<Overlay, Long>, JpaSpecificationExecutor<Overlay> {

    Optional<Overlay> findByOverlayId(String overlayId);

    @EntityGraph(attributePaths = {"platform", "game", "authorUser"})
    Optional<Overlay> findWithDetailsByOverlayId(String overlayId);

    Optional<Overlay> findByCode(String code);

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, Long id);
}
