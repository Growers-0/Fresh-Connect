package hekireki.sanjijiksong.domain.openapi.service;

import hekireki.sanjijiksong.domain.openapi.Repository.PriceDailySearchRepository;
import hekireki.sanjijiksong.domain.openapi.document.PriceDailyDocument;
import hekireki.sanjijiksong.domain.openapi.dto.PriceHistory;
import hekireki.sanjijiksong.domain.openapi.dto.PriceInfo;
import hekireki.sanjijiksong.domain.openapi.dto.ProductPriceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PriceSearchService {

    private final PriceDailySearchRepository searchRepository;

    /**
     * 기본 키워드 검색
     */
    @Cacheable(value = "priceSearchCache", key = "#keyword")
    public List<PriceDailyDocument> searchByItemName(String keyword) {
        return searchRepository.findByItemNameContaining(keyword);
    }

    /**
     * 가격 히스토리 포함 상품 조회
     */
    public List<ProductPriceResponse> searchPriceInfoByKeyword(String keyword, LocalDate startDay, LocalDate endDay) {
        // fallback 대비 extended 조회 범위
        LocalDate extendedStart = (!startDay.isAfter(endDay.minusMonths(1)))
                ? startDay
                : endDay.minusMonths(1);

        // Elasticsearch에서 extended 범위 데이터 조회
        List<PriceDailyDocument> fullList = searchRepository.findByItemNameContainingAndSnapshotDateBetween(
                keyword, extendedStart, endDay
        );

        List<PriceDailyDocument> historyList = fullList.stream()
                .filter(doc -> !doc.getSnapshotDate().isBefore(startDay) && !doc.getSnapshotDate().isAfter(endDay))
                .toList();

        // 단위 + 품종으로 그룹핑
        Map<PriceGroupKey, List<PriceDailyDocument>> grouped = fullList.stream()
                .collect(Collectors.groupingBy(doc -> new PriceGroupKey(doc.getUnit(), doc.getKindName())));

        List<ProductPriceResponse> responses = new ArrayList<>();

        for (Map.Entry<PriceGroupKey, List<PriceDailyDocument>> entry : grouped.entrySet()) {
            PriceGroupKey key = entry.getKey();
            List<PriceDailyDocument> groupList = entry.getValue();
            groupList.sort(Comparator.comparing(PriceDailyDocument::getSnapshotDate));

            // 현재 가격과 과거 가격 조회
            PriceDailyDocument currentRecord = getLatestRecord(groupList, endDay);
            Integer currentPrice = (currentRecord != null) ? currentRecord.getPrice() : null;
            Integer oneDayAgoPrice = getPriceAtOrFallback(groupList, endDay.minusDays(1));
            Integer oneWeekAgoPrice = getPriceAtOrFallback(groupList, endDay.minusWeeks(1));
            Integer oneMonthAgoPrice = getPriceAtOrFallback(groupList, endDay.minusMonths(1));

            String itemName = (currentRecord != null) ? currentRecord.getItemName() : "";

            PriceInfo info = PriceInfo.builder()
                    .categoryCode(currentRecord != null ? currentRecord.getCategoryCode() : "")
                    .itemCode(currentRecord != null ? currentRecord.getItemCode() : "")
                    .itemName(itemName)
                    .kindName(key.getKindName())
                    .unit(key.getUnit())
                    .currentPrice(currentPrice)
                    .oneDayAgoPrice(oneDayAgoPrice)
                    .oneWeekAgoPrice(oneWeekAgoPrice)
                    .oneMonthAgoPrice(oneMonthAgoPrice)
                    .startDate(startDay.toString())
                    .endDate(endDay.toString())
                    .build();

            // 기간 내 히스토리 추출
            List<PriceHistory> history = new ArrayList<>(historyList.stream()
                    .filter(doc -> doc.getUnit().equals(key.getUnit()) && doc.getKindName().equals(key.getKindName()))
                    .sorted(Comparator.comparing(PriceDailyDocument::getSnapshotDate))
                    .collect(Collectors.toMap(
                            PriceDailyDocument::getSnapshotDate, // 키로 snapshotDate 사용
                            doc -> PriceHistory.builder()
                                    .date(doc.getSnapshotDate().toString())
                                    .price(doc.getPrice())
                                    .build(),
                            (existing, replacement) -> existing // 중복된 날짜가 있을 경우 기존 값을 유지
                    ))
                    .values());

            responses.add(ProductPriceResponse.builder()
                    .info(info)
                    .history(history)
                    .build());
        }

        return responses;
    }

    // Helper 메소드들
    private PriceDailyDocument getLatestRecord(List<PriceDailyDocument> list, LocalDate targetDate) {
        PriceDailyDocument result = null;
        for (PriceDailyDocument doc : list) {
            if (!doc.getSnapshotDate().isAfter(targetDate)) {
                result = doc;
            } else {
                break;
            }
        }
        return result;
    }

    private Integer getPriceAtOrFallback(List<PriceDailyDocument> list, LocalDate targetDate) {
        PriceDailyDocument record = getLatestRecord(list, targetDate);
        if (record == null && !list.isEmpty()) {
            record = list.get(0);
        }
        return (record != null) ? record.getPrice() : null;
    }

    private static class PriceGroupKey {
        private final String unit;
        private final String kindName;

        public PriceGroupKey(String unit, String kindName) {
            this.unit = unit;
            this.kindName = kindName;
        }

        public String getUnit() {
            return unit;
        }

        public String getKindName() {
            return kindName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof PriceGroupKey that)) return false;
            return Objects.equals(unit, that.unit) && Objects.equals(kindName, that.kindName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(unit, kindName);
        }
    }
}
