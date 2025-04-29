package hekireki.sanjijiksong.domain.store.dto;

import hekireki.sanjijiksong.domain.store.entity.Store;

/**
 * Store 엔티티의 필요한 필드만 선택적으로 조회하기 위한 프로젝션 DTO
 */
public record StoreProjection(
        Long id,
        String name,
        String address,
        String description,
        String image,
        Boolean active
) {
    /**
     * Store 엔티티로부터 StoreProjection을 생성합니다.
     */
    public static StoreProjection from(Store store) {
        return new StoreProjection(
                store.getId(),
                store.getName(),
                store.getAddress(),
                store.getDescription(),
                store.getImage(),
                store.getActive()
        );
    }
    
    /**
     * StoreProjection을 StoreResponse로 변환합니다.
     */
    public StoreResponse toResponse() {
        return new StoreResponse(
                id,
                name,
                address,
                description,
                image,
                active
        );
    }
} 