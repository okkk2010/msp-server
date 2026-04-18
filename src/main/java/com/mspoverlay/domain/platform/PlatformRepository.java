package com.mspoverlay.domain.platform;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlatformRepository extends JpaRepository<Platform, Long> {

    List<Platform> findAllByActiveTrueOrderByNameAsc();

    Optional<Platform> findBySlug(String slug);
}
