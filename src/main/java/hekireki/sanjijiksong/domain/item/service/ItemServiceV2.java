package hekireki.sanjijiksong.domain.item.service;

import hekireki.sanjijiksong.domain.item.dto.ItemSalesOverviewResponse;
import hekireki.sanjijiksong.domain.item.dto.ItemSalesSummaryDTO;
import hekireki.sanjijiksong.domain.item.dto.Series;
import hekireki.sanjijiksong.domain.item.repository.ItemRepository;
import hekireki.sanjijiksong.domain.order.repository.OrderListRepository;
import hekireki.sanjijiksong.domain.store.entity.Store;
import hekireki.sanjijiksong.domain.store.repository.StoreRepository;
import hekireki.sanjijiksong.domain.user.entity.User;
import hekireki.sanjijiksong.domain.user.repository.UserRepository;
import hekireki.sanjijiksong.global.common.exception.ErrorCode;
import hekireki.sanjijiksong.global.common.exception.StoreException;
import hekireki.sanjijiksong.global.common.exception.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ItemServiceV2 {

    private final OrderListRepository orderListRepository;
    private final ItemValidationService itemValidationService;

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

//    //응답을 dto로 변경한 버전
//    public ResponseEntity<ItemSalesOverviewResponse> getSalesOverviewV2test(String email) {
//
//        Store userStore = itemValidationService.verifyAndGetStore(email);
//
//        //판매량
//        List<ItemSalesSummaryDTO> summaries = orderListRepository.findItemSalesSummaryByStoreId(userStore.getId());
//
//        List<String> itemNames = summaries.stream().map(ItemSalesSummaryDTO::itemName).toList();
//        List<Long> revenues = summaries.stream().map(ItemSalesSummaryDTO::summary).toList();
//
//        ItemSalesOverviewResponse response = new ItemSalesOverviewResponse(
//                "품목 매출 현황",
//                itemNames,
//                List.of(new Series("품목 별 매출",revenues)));
//
//        return ResponseEntity.ok(response);
//    }

    public ResponseEntity<?> getTop5BestSellingProducts(String email) {

        Store userStore = itemValidationService.verifyAndGetStore(email);

        List<ItemSalesSummaryDTO> summaries = orderListRepository.findItemSalesSummaryByStoreId(userStore.getId(), PageRequest.of(0, 5));

        List<String> top5ItemNames = new ArrayList<>();
        List<Integer> top5Revenues = new ArrayList<>();

        List<String> itemNames = summaries.stream().map(ItemSalesSummaryDTO::itemName).toList();
        List<Long> revenues = summaries.stream().map(ItemSalesSummaryDTO::summary).toList();

        ItemSalesOverviewResponse response = new ItemSalesOverviewResponse(
                "인기 제품 매출 현황",
                itemNames,
                List.of(new Series("품목 별 매출",revenues)));

        // ResponseEntity로 반환
        return ResponseEntity.ok(response);
    }

}
