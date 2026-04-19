package com.mspoverlay.domain.platform;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PlatformService {

    private final PlatformRepository platformRepository;

    public PlatformService(PlatformRepository platformRepository) {
        this.platformRepository = platformRepository;
    }

    public List<PlatformResponse> getPlatforms() {
        return platformRepository.findAllByActiveTrueOrderByNameAsc()
                .stream()
                .map(PlatformResponse::from)
                .toList();
    }
}
