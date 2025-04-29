package hekireki.sanjijiksong.domain.store.controller;

import hekireki.sanjijiksong.domain.store.dto.StoreResponse;
import hekireki.sanjijiksong.domain.store.service.StoreSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/stores/elastic")
@RequiredArgsConstructor
@Tag(name = "StoreSearchController", description = "가게 검색 API (Elasticsearch)")
public class StoreSearchController {

    private final StoreSearchService storeSearchService;

    @Operation(summary = "전체 데이터 Elasticsearch 동기화", description = "모든 가게 데이터를 Elasticsearch에 동기화합니다.")
    @ApiResponse(responseCode = "200", description = "동기화 성공")
    @PostMapping("/sync")
    public ResponseEntity<String> syncAllData() {
        storeSearchService.syncStoreDataToElasticsearch();
        return ResponseEntity.ok("All store data synchronized to Elasticsearch");
    }

    @Operation(summary = "가게 키워드 검색 (Elasticsearch)", description = "키워드로 가게를 검색합니다.")
    @ApiResponse(responseCode = "200", description = "검색 성공")
    @GetMapping("/search/keyword")
    public ResponseEntity<List<StoreResponse>> searchByKeyword(@RequestParam("keyword") String keyword) {
        List<StoreResponse> results = storeSearchService.searchStoresByKeyword(keyword);
        log.info("Elasticsearch 키워드 검색 - keyword: {}, 결과 수: {}", keyword, results.size());
        return ResponseEntity.ok(results);
    }

    @Operation(summary = "가게 주소 검색 (Elasticsearch)", description = "주소로 가게를 검색합니다.")
    @ApiResponse(responseCode = "200", description = "검색 성공")
    @GetMapping("/search/location")
    public ResponseEntity<List<StoreResponse>> searchByLocation(@RequestParam("location") String location) {
        List<StoreResponse> results = storeSearchService.searchStoresByLocation(location);
        log.info("Elasticsearch 주소 검색 - location: {}, 결과 수: {}", location, results.size());
        return ResponseEntity.ok(results);
    }

    @Operation(summary = "통합 검색 (Elasticsearch)", description = "이름, 주소, 설명 등 전체 필드에서 검색합니다.")
    @ApiResponse(responseCode = "200", description = "검색 성공")
    @GetMapping("/search")
    public ResponseEntity<List<StoreResponse>> searchStores(@RequestParam("query") String query) {
        List<StoreResponse> results = storeSearchService.searchStores(query);
        log.info("Elasticsearch 통합 검색 - query: {}, 결과 수: {}", query, results.size());
        return ResponseEntity.ok(results);
    }
} 