package hekireki.sanjijiksong.domain.store.mapper;

import hekireki.sanjijiksong.domain.store.dto.StoreResponse;
import hekireki.sanjijiksong.domain.store.entity.Store;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class StoreMapper {
    
    public StoreResponse storeToStoreResponse(Store store) {
        if (store == null) {
            return null;
        }
        
        return new StoreResponse(
            store.getId(),
            store.getName(),
            store.getAddress(),
            store.getDescription(),
            store.getImage(),
            store.getActive()
        );
    }
    
    public List<StoreResponse> storesToStoreResponses(List<Store> stores) {
        if (stores == null) {
            return null;
        }
        
        return stores.stream()
                .map(this::storeToStoreResponse)
                .collect(Collectors.toList());
    }
} 