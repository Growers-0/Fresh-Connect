package hekireki.sanjijiksong.domain.item.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import hekireki.sanjijiksong.domain.item.controller.ItemControllerV2;
import hekireki.sanjijiksong.domain.item.dto.*;

import hekireki.sanjijiksong.domain.item.es.ItemDocument;
import hekireki.sanjijiksong.domain.item.repository.ItemSearchRepository;
import hekireki.sanjijiksong.domain.openapi.document.PriceDailyDocument;
import hekireki.sanjijiksong.domain.order.repository.OrderListRepository;
import hekireki.sanjijiksong.domain.store.entity.Store;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceV2 {

    private final OrderListRepository orderListRepository;
    private final ItemValidationService itemValidationService;
    private final ItemSearchRepository itemSearchRepository;
    private final ElasticsearchClient elasticsearchClient;

    //쿼리 개선
    //응답값 DTO로 변경
    public ResponseEntity<ItemSalesOverviewResponse> getSalesOverviewV2(String email) {
        Store userStore = itemValidationService.verifyAndGetStore(email);

        //판매량
        List<ItemSalesSummaryDTO> summaries = orderListRepository.findItemSalesSummaryByStoreId(userStore.getId());
        List<String> itemNames = summaries.stream().map(ItemSalesSummaryDTO::itemName).toList();
        List<Long> revenues = summaries.stream().map(ItemSalesSummaryDTO::summary).toList();

        ItemSalesOverviewResponse response = new ItemSalesOverviewResponse(
                "품목 매출 현황",
                itemNames,
                List.of(new Series("품목 별 매출",revenues)));

        return ResponseEntity.ok(response);
    }

    //쿼리 개선
    //페이징으로 상위 n개를 가져올 수 있게 개선
    //응답 값 변경
    public ResponseEntity<?> getTop5BestSellingProducts(String email) {
        Store userStore = itemValidationService.verifyAndGetStore(email);

        List<ItemSalesSummaryDTO> summaries = orderListRepository.findItemSalesSummaryByStoreId(userStore.getId(), PageRequest.of(0, 5));
        List<String> itemNames = summaries.stream().map(ItemSalesSummaryDTO::itemName).toList();
        List<Long> revenues = summaries.stream().map(ItemSalesSummaryDTO::summary).toList();

        ItemSalesOverviewResponse response = new ItemSalesOverviewResponse(
                "인기 제품 매출 현황",
                itemNames,
                List.of(new Series("품목 별 매출",revenues)));

        // ResponseEntity로 반환
        return ResponseEntity.ok(response);
    }

    //쿼리 개선 Cast로 날짜 별 집계
    //데이터 범위 조절 가능 n일 이내
    //응답 값 개선
    public ResponseEntity<?> getWeeklySalesTrend(String email, int recentDays) {
        Store userStore = itemValidationService.verifyAndGetStore(email);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime startDay = LocalDateTime.now().minusDays(recentDays-1).toLocalDate().atStartOfDay();
        LocalDateTime endDay = LocalDateTime.now().toLocalDate().atTime(LocalTime.MAX);

        List<ItemDateStatistics> summaries = orderListRepository.findSalesSummaryTotal(userStore.getId(),startDay,endDay);
        List<Long> revenues = summaries.stream().map(ItemDateStatistics::getRevenue).toList();
        List<String> dates = summaries.stream()
                .map(d -> d.getDate().format(formatter))
                .toList();

        ItemSalesTrendResponse response = new ItemSalesTrendResponse(
                "주간 매출 추이",
                dates,
                List.of(new Series("매출",revenues))
        );

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> getDailyHourlySales(String email, LocalDate localDate) {

        Store userStore = itemValidationService.verifyAndGetStore(email);

        LocalDateTime startOfDay = localDate.atStartOfDay(); // 00:00:00
        LocalDateTime endOfDay = localDate.atTime(LocalTime.MAX); // 23:59:59.999999999

        List<ItemHourStatistics> summaries = orderListRepository.findHourlySalesByStoreIdAndDate(userStore.getId(),startOfDay,endOfDay);
        List<Long> revenues = summaries.stream().map(ItemHourStatistics::getRevenue).toList();
        List<String> hour = summaries.stream().map(h -> String.valueOf(h.getHour())).toList();

        ItemSalesTrendResponse response = new ItemSalesTrendResponse(
                "시간별 매출 추이",
                hour,
                List.of(new Series("매출",revenues))
        );

        return ResponseEntity.ok(response);

    }

    public List<ItemDocument> searchByItemName(String keyword) {
        return itemSearchRepository.searchByNameOnlyActive(keyword);
    }

    public List<String> autoCompleteItemNames(String prefix) throws IOException {
        SearchResponse<ItemDocument> response = elasticsearchClient.search(s -> s
                .index("items")
                .query(q -> q
                        .matchPhrasePrefix(m -> m
                                .field("name")
                                .query(prefix)
                        )
                )
                .size(10), ItemDocument.class);

        return response.hits().hits().stream()
                .map(hit -> hit.source().getName())
                .distinct()
                .collect(Collectors.toList());
    }

}
