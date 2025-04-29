package hekireki.sanjijiksong.domain.store.listener;

import hekireki.sanjijiksong.domain.store.entity.Store;
import hekireki.sanjijiksong.domain.store.service.StoreSearchService;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StoreEntityListener {

    private static StoreSearchService storeSearchService;

    @Autowired
    public void setStoreSearchService(StoreSearchService storeSearchService) {
        StoreEntityListener.storeSearchService = storeSearchService;
    }

    @PostPersist
    public void postPersist(Store store) {
        log.info("Store created: {}", store.getId());
        if (storeSearchService != null) {
            storeSearchService.syncStoreToElasticsearch(store);
        }
    }

    @PostUpdate
    public void postUpdate(Store store) {
        log.info("Store updated: {}", store.getId());
        if (storeSearchService != null) {
            storeSearchService.syncStoreToElasticsearch(store);
        }
    }

    @PostRemove
    public void postRemove(Store store) {
        log.info("Store removed: {}", store.getId());
        // ElasticSearch에서도 데이터 삭제 로직 구현 가능
    }
} 