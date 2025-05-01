package hekireki.sanjijiksong.domain.following.dto;

import hekireki.sanjijiksong.domain.store.dto.StoreResponse;
import hekireki.sanjijiksong.domain.following.entity.Following;

import java.time.LocalDateTime;

public record FollowingResponse(
    Long id,
    Long userId,
    StoreResponse store,
    LocalDateTime createdAt
) {
    public static FollowingResponse from(Following following) {
        return new FollowingResponse(
            following.getId(),
            following.getUser().getId(),
            StoreResponse.of(following.getStore()),
            following.getCreatedAt()
        );
    }
} 