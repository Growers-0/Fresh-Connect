package hekireki.sanjijiksong.domain.store.dto;

import hekireki.sanjijiksong.domain.store.entity.Store;
import hekireki.sanjijiksong.domain.store.mapper.StoreMapper;

import java.util.List;

public record StoreResponse(
        Long id,
        String name,
        String address,
        String description,
        String image,
        Boolean active
) {
    public static StoreResponse of(Store store) {
        return new StoreResponse(
                store.getId(),
                store.getName(),
                store.getAddress(),
                store.getDescription(),
                store.getImage(),
                store.getActive()
        );
    }
    
    public static StoreResponse fromEntity(Store store, StoreMapper mapper) {
        return mapper.storeToStoreResponse(store);
    }
    
    public static List<StoreResponse> fromEntities(List<Store> stores, StoreMapper mapper) {
        return mapper.storesToStoreResponses(stores);
    }
}
