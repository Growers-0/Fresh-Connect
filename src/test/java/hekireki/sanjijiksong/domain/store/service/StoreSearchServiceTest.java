package hekireki.sanjijiksong.domain.store.service;

import hekireki.sanjijiksong.domain.store.document.StoreDocument;
import hekireki.sanjijiksong.domain.store.dto.StoreResponse;
import hekireki.sanjijiksong.domain.store.entity.Store;
import hekireki.sanjijiksong.domain.store.repository.StoreRepository;
import hekireki.sanjijiksong.domain.store.repository.search.StoreSearchRepository;
import hekireki.sanjijiksong.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoreSearchServiceTest {

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private StoreSearchRepository storeSearchRepository;

    @InjectMocks
    private StoreSearchService storeSearchService;

    @Captor
    private ArgumentCaptor<List<StoreDocument>> storeDocumentsCaptor;

    private User testUser;
    private Store testStore1;
    private Store testStore2;
    private StoreDocument testStoreDocument1;
    private StoreDocument testStoreDocument2;

    @BeforeEach
    void setUp() {
        // 테스트용 사용자 생성
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .nickname("테스트유저")
                .active(true)
                .build();

        // 테스트용 가게 엔티티 생성
        testStore1 = Store.builder()
                .id(1L)
                .user(testUser)
                .name("카페 산지")
                .address("서울시 강남구")
                .description("신선한 과일 판매")
                .image("image1.jpg")
                .active(true)
                .build();

        testStore2 = Store.builder()
                .id(2L)
                .user(testUser)
                .name("과일가게")
                .address("서울시 송파구")
                .description("유기농 과일")
                .image("image2.jpg")
                .active(true)
                .build();

        // 테스트용 StoreDocument 생성
        testStoreDocument1 = StoreDocument.fromEntity(testStore1);
        testStoreDocument2 = StoreDocument.fromEntity(testStore2);
    }

    @Test
    @DisplayName("모든 가게 데이터 Elasticsearch 동기화 테스트")
    void syncStoreDataToElasticsearch_Success() {
        // given
        List<Store> stores = Arrays.asList(testStore1, testStore2);
        when(storeRepository.findAll()).thenReturn(stores);
        when(storeSearchRepository.saveAll(any())).thenReturn(List.of(testStoreDocument1, testStoreDocument2));

        // when
        storeSearchService.syncStoreDataToElasticsearch();

        // then
        verify(storeRepository).findAll();
        verify(storeSearchRepository).saveAll(any());
    }

    @Test
    @DisplayName("단일 가게 Elasticsearch 동기화 테스트")
    void syncStoreToElasticsearch_Success() {
        // given
        when(storeSearchRepository.save(any(StoreDocument.class))).thenReturn(testStoreDocument1);

        // when
        storeSearchService.syncStoreToElasticsearch(testStore1);

        // then
        verify(storeSearchRepository).save(any(StoreDocument.class));
    }

    @Test
    @DisplayName("이름으로 가게 검색 테스트")
    void searchStoresByKeyword_Success() {
        // given
        String keyword = "카페";
        List<StoreDocument> storeDocuments = Arrays.asList(testStoreDocument1);
        when(storeSearchRepository.findByNameContainingAndActiveTrue(keyword)).thenReturn(storeDocuments);

        // when
        List<StoreResponse> results = storeSearchService.searchStoresByKeyword(keyword);

        // then
        verify(storeSearchRepository).findByNameContainingAndActiveTrue(keyword);
        assertEquals(1, results.size());
        assertEquals(testStore1.getId(), results.get(0).id());
        assertEquals(testStore1.getName(), results.get(0).name());
    }

    @Test
    @DisplayName("주소로 가게 검색 테스트")
    void searchStoresByLocation_Success() {
        // given
        String location = "강남";
        List<StoreDocument> storeDocuments = Arrays.asList(testStoreDocument1);
        when(storeSearchRepository.findByAddressContainingAndActiveTrue(location)).thenReturn(storeDocuments);

        // when
        List<StoreResponse> results = storeSearchService.searchStoresByLocation(location);

        // then
        verify(storeSearchRepository).findByAddressContainingAndActiveTrue(location);
        assertEquals(1, results.size());
        assertEquals(testStore1.getId(), results.get(0).id());
        assertEquals(testStore1.getAddress(), results.get(0).address());
    }

    // ElasticsearchClient 사용으로 인한 테스트 오류를 피하기 위해 
    // 통합 검색 테스트는 StoreSearchServiceIntegrationTest 클래스에서 수행
} 