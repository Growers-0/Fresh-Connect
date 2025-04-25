package hekireki.sanjijiksong.domain.store.controller;

import hekireki.sanjijiksong.domain.store.dto.StoreResponse;
import hekireki.sanjijiksong.domain.store.service.StoreSearchService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StoreSearchController.class)
class StoreSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StoreSearchService storeSearchService;

    @Test
    @WithMockUser
    @DisplayName("가게 데이터 Elasticsearch 동기화 테스트")
    void syncAllData() throws Exception {
        // when
        mockMvc.perform(post("/api/v1/stores/elastic/sync")
                .contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isOk());

        verify(storeSearchService).syncStoreDataToElasticsearch();
    }

    @Test
    @DisplayName("키워드로 가게 검색 테스트")
    void searchByKeyword() throws Exception {
        // given
        String keyword = "카페";
        StoreResponse storeResponse = new StoreResponse(1L, "카페 산지", "서울시 강남구", "신선한 과일", "image.jpg", true);
        List<StoreResponse> responses = Collections.singletonList(storeResponse);

        when(storeSearchService.searchStoresByKeyword(keyword)).thenReturn(responses);

        // when & then
        mockMvc.perform(get("/api/v1/stores/elastic/search/keyword")
                .param("keyword", keyword)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("카페 산지"));
    }

    @Test
    @DisplayName("주소로 가게 검색 테스트")
    void searchByLocation() throws Exception {
        // given
        String location = "강남";
        StoreResponse storeResponse = new StoreResponse(1L, "카페 산지", "서울시 강남구", "신선한 과일", "image.jpg", true);
        List<StoreResponse> responses = Collections.singletonList(storeResponse);

        when(storeSearchService.searchStoresByLocation(location)).thenReturn(responses);

        // when & then
        mockMvc.perform(get("/api/v1/stores/elastic/search/location")
                .param("location", location)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].address").value("서울시 강남구"));
    }

    @Test
    @DisplayName("통합 검색 테스트")
    void searchStores() throws Exception {
        // given
        String query = "과일";
        StoreResponse storeResponse1 = new StoreResponse(1L, "카페 산지", "서울시 강남구", "신선한 과일", "image1.jpg", true);
        StoreResponse storeResponse2 = new StoreResponse(2L, "과일가게", "서울시 송파구", "유기농 과일", "image2.jpg", true);
        List<StoreResponse> responses = Arrays.asList(storeResponse1, storeResponse2);

        when(storeSearchService.searchStores(query)).thenReturn(responses);

        // when & then
        mockMvc.perform(get("/api/v1/stores/elastic/search")
                .param("query", query)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("과일가게"));
    }

    @Test
    @DisplayName("빈 검색 결과 반환 테스트")
    void searchWithNoResults() throws Exception {
        // given
        String query = "존재하지 않는 키워드";
        when(storeSearchService.searchStores(anyString())).thenReturn(Collections.emptyList());

        // when & then
        mockMvc.perform(get("/api/v1/stores/elastic/search")
                .param("query", query)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
} 