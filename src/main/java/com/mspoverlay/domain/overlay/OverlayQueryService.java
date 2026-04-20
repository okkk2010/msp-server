package com.mspoverlay.domain.overlay;

import java.util.regex.Pattern;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mspoverlay.global.exception.BusinessException;
import com.mspoverlay.global.exception.ErrorCode;
import com.mspoverlay.global.response.PageResponse;
import com.mspoverlay.infrastructure.storage.OverlayStorageService;
import jakarta.persistence.criteria.JoinType;

@Service
@Transactional(readOnly = true)
public class OverlayQueryService {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;
    private static final Pattern OVERLAY_CODE_PATTERN = Pattern.compile("^[A-Z0-9]{6}$");

    private final OverlayRepository overlayRepository;
    private final OverlayStorageService overlayStorageService;
    private final ObjectMapper objectMapper;

    public OverlayQueryService(
            OverlayRepository overlayRepository,
            OverlayStorageService overlayStorageService,
            ObjectMapper objectMapper
    ) {
        this.overlayRepository = overlayRepository;
        this.overlayStorageService = overlayStorageService;
        this.objectMapper = objectMapper;
    }

    public PageResponse<OverlaySummaryResponse> getOverlays(
            Integer page,
            Integer size,
            String keyword,
            String platform,
            String game,
            String code,
            String sort
    ) {
        PageRequest pageRequest = PageRequest.of(normalizePage(page), normalizeSize(size), resolveSort(sort));
        Specification<Overlay> specification = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
        for (Specification<Overlay> candidate : new Specification[] {
                keywordContains(keyword),
                platformEquals(platform),
                gameMatches(game),
                codeEquals(code)
        }) {
            if (candidate != null) {
                specification = specification.and(candidate);
            }
        }

        return PageResponse.from(
                overlayRepository.findAll(specification, pageRequest)
                        .map(OverlaySummaryResponse::from)
        );
    }

    public OverlayDetailResponse getOverlayDetail(String overlayId) {
        return overlayRepository.findWithDetailsByOverlayId(overlayId)
                .map(OverlayDetailResponse::from)
                .orElseThrow(() -> new BusinessException(ErrorCode.OVERLAY_NOT_FOUND));
    }

    public OverlayCodeLoadResponse getOverlayByCode(String code) {
        String normalizedCode = normalizeOverlayCode(code);
        Overlay overlay = overlayRepository.findWithDetailsByCode(normalizedCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.OVERLAY_NOT_FOUND, "overlay not found"));
        JsonNode overlayJson = readOverlayJson(overlay.getJsonPath());
        return OverlayCodeLoadResponse.from(overlay, overlayJson);
    }

    private int normalizePage(Integer page) {
        if (page == null) {
            return DEFAULT_PAGE;
        }
        if (page < 0) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "page는 0 이상이어야 합니다.");
        }
        return page;
    }

    private int normalizeSize(Integer size) {
        if (size == null) {
            return DEFAULT_SIZE;
        }
        if (size <= 0 || size > MAX_SIZE) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "size는 1 이상 100 이하여야 합니다.");
        }
        return size;
    }

    private Sort resolveSort(String sort) {
        if (sort == null || sort.isBlank() || sort.equals("newest")) {
            return Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id"));
        }
        if (sort.equals("updated")) {
            return Sort.by(Sort.Order.desc("updatedAt"), Sort.Order.desc("id"));
        }
        throw new BusinessException(ErrorCode.INVALID_INPUT, "sort는 newest 또는 updated만 지원합니다.");
    }

    private Specification<Overlay> keywordContains(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }

        String normalizedKeyword = "%" + keyword.trim().toLowerCase() + "%";
        return (root, query, criteriaBuilder) -> {
            var authorJoin = root.join("authorUser", JoinType.INNER);
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), normalizedKeyword),
                    criteriaBuilder.like(criteriaBuilder.lower(criteriaBuilder.coalesce(root.get("description"), "")), normalizedKeyword),
                    criteriaBuilder.like(criteriaBuilder.lower(authorJoin.get("name")), normalizedKeyword)
            );
        };
    }

    private Specification<Overlay> platformEquals(String platform) {
        if (platform == null || platform.isBlank()) {
            return null;
        }

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(criteriaBuilder.lower(root.join("platform", JoinType.INNER).get("slug")), platform.trim().toLowerCase());
    }

    private Specification<Overlay> gameMatches(String game) {
        if (game == null || game.isBlank()) {
            return null;
        }

        String trimmedGame = game.trim();
        return (root, query, criteriaBuilder) -> {
            var gameJoin = root.join("game", JoinType.LEFT);
            if (trimmedGame.chars().allMatch(Character::isDigit)) {
                return criteriaBuilder.equal(gameJoin.get("id"), Long.parseLong(trimmedGame));
            }
            return criteriaBuilder.equal(criteriaBuilder.lower(gameJoin.get("slug")), trimmedGame.toLowerCase());
        };
    }

    private Specification<Overlay> codeEquals(String code) {
        if (code == null || code.isBlank()) {
            return null;
        }

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(criteriaBuilder.upper(root.get("code")), code.trim().toUpperCase());
    }

    private String normalizeOverlayCode(String code) {
        if (code == null) {
            throw new BusinessException(ErrorCode.INVALID_OVERLAY_CODE);
        }

        String normalizedCode = code.trim().toUpperCase();
        if (!OVERLAY_CODE_PATTERN.matcher(normalizedCode).matches()) {
            throw new BusinessException(ErrorCode.INVALID_OVERLAY_CODE);
        }
        return normalizedCode;
    }

    private JsonNode readOverlayJson(String jsonPath) {
        try {
            return objectMapper.readTree(overlayStorageService.readOverlayJson(jsonPath));
        } catch (JsonProcessingException exception) {
            throw new BusinessException(ErrorCode.OVERLAY_JSON_NOT_FOUND);
        }
    }
}
