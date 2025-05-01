package hekireki.sanjijiksong.domain.store.repository.search;

import hekireki.sanjijiksong.domain.store.document.StoreDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreSearchRepository extends ElasticsearchRepository<StoreDocument, Long> {
    
    // 검색 메서드
    List<StoreDocument> findByNameContainingAndActiveTrue(String keyword);
    
    List<StoreDocument> findByAddressContainingAndActiveTrue(String location);
    
    List<StoreDocument> findByNameContainingOrAddressContainingAndActiveTrue(String keyword, String address);
} 