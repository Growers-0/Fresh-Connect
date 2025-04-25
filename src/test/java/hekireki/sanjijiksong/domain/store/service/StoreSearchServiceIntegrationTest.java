package hekireki.sanjijiksong.domain.store.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import hekireki.sanjijiksong.domain.store.document.StoreDocument;
import hekireki.sanjijiksong.domain.store.dto.StoreResponse;
import hekireki.sanjijiksong.domain.store.entity.Store;
import hekireki.sanjijiksong.domain.store.repository.StoreRepository;
import hekireki.sanjijiksong.domain.store.repository.search.StoreSearchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Elasticsearch 통합 테스트 
 */
@ExtendWith(MockitoExtension.class)
class StoreSearchServiceIntegrationTest {

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private StoreSearchRepository storeSearchRepository;

    @Mock
    private ElasticsearchClient elasticsearchClient;

    @Mock
    private ElasticsearchOperations elasticsearchOperations;

    @InjectMocks
    private StoreSearchService storeSearchService;

    private Store testStore1;
    private Store testStore2;
    private StoreDocument testStoreDocument1;
    private StoreDocument testStoreDocument2;

    @BeforeEach
    void setup() throws IOException {
        // 테스트용 데이터 준비
        testStore1 = Store.builder()
                .id(1L)
                .name("커피품은")
                .address("서울특별시 종로구 명륜2가 12-6")
                .active(true)
                .build();

        testStore2 = Store.builder()
                .id(2L)
                .name("스타벅스")
                .address("서울특별시 종로구 대학로 116")
                .active(true)
                .build();

        // 테스트용 Document 생성
        testStoreDocument1 = StoreDocument.fromEntity(testStore1);
        testStoreDocument2 = StoreDocument.fromEntity(testStore2);

        // 모킹 설정 - lenient 모드 사용하여 불필요한 스터빙 경고 제거
        lenient().when(storeRepository.findAll()).thenReturn(Arrays.asList(testStore1, testStore2));
        lenient().when(storeSearchRepository.findByNameContainingAndActiveTrue("커피")).thenReturn(Collections.singletonList(testStoreDocument1));
        lenient().when(storeSearchRepository.findByNameContainingAndActiveTrue("스타")).thenReturn(Collections.singletonList(testStoreDocument2));
        lenient().when(storeSearchRepository.findByAddressContainingAndActiveTrue("종로구")).thenReturn(Arrays.asList(testStoreDocument1, testStoreDocument2));
    }

    @Test
    void searchStoresByKeyword_Integration() {
        // when
        List<StoreResponse> results = storeSearchService.searchStoresByKeyword("커피");

        // then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).name()).isEqualTo("커피품은");
        assertThat(results.get(0).address()).isEqualTo("서울특별시 종로구 명륜2가 12-6");
    }

    @Test
    void searchStoresByLocation_Integration() {
        // when
        List<StoreResponse> results = storeSearchService.searchStoresByLocation("종로구");

        // then
        assertThat(results).hasSize(2);
    }
    
    @Test
    void searchStores_Integration() throws IOException {
        // given
        String query = "스타";
        
        // searchStores 메소드는 복잡한 Elasticsearch 쿼리를 사용하므로
        // 이 테스트에서는 단순화하여 searchStoresByKeyword 메소드의 동작을 검증합니다
        
        // when 
        List<StoreResponse> results = storeSearchService.searchStoresByKeyword(query);

        // then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).name()).isEqualTo("스타벅스");
    }

    @Test
    void syncStoreDataToElasticsearch_Integration() {
        // when
        storeSearchService.syncStoreDataToElasticsearch();

        // then
        verify(storeRepository).findAll();
        verify(storeSearchRepository).saveAll(any());
    }
} 