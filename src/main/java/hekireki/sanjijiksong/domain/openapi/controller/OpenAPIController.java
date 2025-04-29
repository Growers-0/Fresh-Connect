package hekireki.sanjijiksong.domain.openapi.controller;

import hekireki.sanjijiksong.domain.openapi.api.OpenApi;
import hekireki.sanjijiksong.domain.openapi.dto.ProductPriceResponse;
import hekireki.sanjijiksong.domain.openapi.dto.TrendingKeywordPrice;
import hekireki.sanjijiksong.domain.openapi.service.KamisPriceImportService;
import hekireki.sanjijiksong.domain.openapi.service.ProductPriceService;
import hekireki.sanjijiksong.domain.openapi.service.OpenApiScheduler;
import hekireki.sanjijiksong.domain.openapi.service.TrendingKeywordService;
import hekireki.sanjijiksong.global.common.exception.KamisException;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/openapi")
@Validated
public class OpenAPIController implements OpenApi {
    private final KamisPriceImportService kamisPriceImportService;
    private final ProductPriceService productPriceService;
    private final TrendingKeywordService trendingKeywordService;


    // KAMIS API를 통해 가격 정보를 가져와 저장
    @GetMapping("/kamis/prices")
    public CompletableFuture<ResponseEntity<?>> getPrice(@RequestParam(name = "category_code") String categoryCode,
                                                         @RequestParam(name = "regday") String regDay) {
        return kamisPriceImportService.getPrices(categoryCode, regDay)
                .thenApply(aVoid -> ResponseEntity.ok().build());
    }

    @GetMapping("/kamis/allprices")
    public CompletableFuture<ResponseEntity<?>> getAllPrice(@RequestParam(name = "start_day") String startDay,
                                         @RequestParam(name = "end_day") String endDay) {
        LocalDate start = LocalDate.parse(startDay);
        LocalDate end = LocalDate.parse(endDay);
        return kamisPriceImportService.getAllPricesBetween(start, end)
                .thenApply(aVoid -> ResponseEntity.ok("All price data fetched successfully"));
    }

    @GetMapping("/getPrices")
    public CompletableFuture<ResponseEntity<?>> getPrices(
            @RequestParam("item_code") @NotBlank String itemCode,
            @RequestParam("category_code") @NotBlank String categoryCode,
            @RequestParam("start_date") @NotBlank String startDate,
            @RequestParam("end_date") @NotBlank String endDate) {

        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        long dayDiffrence = ChronoUnit.DAYS.between(start, end);
        if (dayDiffrence > 30) {
            throw new KamisException.PriceQueryPeriodTooLongException();
        }

        // 비동기 처리를 위해 CompletableFuture로 감싸서 반환
        return CompletableFuture.supplyAsync(() -> {
            List<ProductPriceResponse> response = productPriceService.getPriceInfo(start, end, categoryCode, itemCode);
            return ResponseEntity.ok(response);
        });
    }

    @GetMapping("/naver/crawling")
    public ResponseEntity<?> getCrawler(){
        Instant start = Instant.now();
        trendingKeywordService.saveTodayTrendingKeywords();
        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);

        log.info("실행 시간: {}초 {}ms", duration.getSeconds(), duration.toMillisPart());
        return ResponseEntity.ok("Crawling completed successfully");
    }

    @GetMapping("/naver/trending")
    public ResponseEntity<?> getTrendingKeywords() {
        Map<String, TrendingKeywordPrice> priceInfoForTrendingKeywords = trendingKeywordService.getTrendingKeywordsPriceInfo();
        return ResponseEntity.ok(priceInfoForTrendingKeywords);
    }
}
