package hekireki.sanjijiksong.domain.store.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import hekireki.sanjijiksong.domain.store.document.StoreDocument;
import hekireki.sanjijiksong.domain.store.dto.StoreResponse;
import hekireki.sanjijiksong.domain.store.entity.Store;
import hekireki.sanjijiksong.domain.store.repository.StoreRepository;
import hekireki.sanjijiksong.domain.store.repository.search.StoreSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreSearchService {

    private final StoreSearchRepository storeSearchRepository;
    private final StoreRepository storeRepository;
    private final ElasticsearchClient elasticsearchClient;
    private final ElasticsearchOperations elasticsearchOperations;

    // 모든 Store 엔티티를 Elasticsearch에 동기화
    public void syncStoreDataToElasticsearch() {
        log.info("Syncing all store data to Elasticsearch");
        List<Store> allStores = storeRepository.findAll();
        List<StoreDocument> storeDocuments = allStores.stream()
                .map(StoreDocument::fromEntity)
                .toList();
        
        storeSearchRepository.saveAll(storeDocuments);
        log.info("Synced {} stores to Elasticsearch", storeDocuments.size());
    }

    // 단일 Store 엔티티를 Elasticsearch에 동기화
    public void syncStoreToElasticsearch(Store store) {
        log.info("Syncing store with id: {} to Elasticsearch", store.getId());
        StoreDocument document = StoreDocument.fromEntity(store);
        storeSearchRepository.save(document);
    }

    // 키워드로 가게 검색
    public List<StoreResponse> searchStoresByKeyword(String keyword) {
        log.info("Searching stores by keyword: {}", keyword);
        
        // 기본 spring-data 방식 검색
        List<StoreDocument> results = storeSearchRepository.findByNameContainingAndActiveTrue(keyword);
        
        return results.stream()
                .map(this::convertToStoreResponse)
                .toList();
    }

    // 주소로 가게 검색
    public List<StoreResponse> searchStoresByLocation(String location) {
        log.info("Searching stores by location: {}", location);
        
        List<StoreDocument> results = storeSearchRepository.findByAddressContainingAndActiveTrue(location);
        
        return results.stream()
                .map(this::convertToStoreResponse)
                .toList();
    }

    // 복합 검색 (키워드와 주소 모두 검색)
    public List<StoreResponse> searchStores(String query) {
        log.info("Advanced search for stores with query: {}", query);
        
        try {
            // 새로운 Elasticsearch Java API 클라이언트 사용
            Query nameQuery = Query.of(q -> q
                .match(m -> m
                    .field("name")
                    .query(query)
                )
            );
            
            Query addressQuery = Query.of(q -> q
                .match(m -> m
                    .field("address")
                    .query(query)
                )
            );
            
            Query descriptionQuery = Query.of(q -> q
                .match(m -> m
                    .field("description")
                    .query(query)
                )
            );
            
            Query activeFilter = Query.of(q -> q
                .term(t -> t
                    .field("active")
                    .value(true)
                )
            );
            
            BoolQuery boolQuery = BoolQuery.of(b -> b
                .should(nameQuery)
                .should(addressQuery)
                .should(descriptionQuery)
                .minimumShouldMatch("1")
                .filter(activeFilter)
            );
            
            SearchResponse<StoreDocument> response = elasticsearchClient.search(s -> s
                .index("stores")
                .query(q -> q.bool(boolQuery)),
                StoreDocument.class
            );
            
            return response.hits().hits().stream()
                .map(Hit::source)
                .map(this::convertToStoreResponse)
                .toList();
                
        } catch (IOException e) {
            log.error("Error during Elasticsearch search: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    // StoreDocument를 StoreResponse로 변환
    private StoreResponse convertToStoreResponse(StoreDocument document) {
        if (document == null) {
            return null;
        }
        return new StoreResponse(
                document.getId(),
                document.getName(),
                document.getAddress(),
                document.getDescription(),
                document.getImage(),
                document.getActive()
        );
    }
} 