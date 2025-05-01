package hekireki.sanjijiksong.domain.openapi.controller;

import hekireki.sanjijiksong.domain.openapi.document.PriceDailyDocument;
import hekireki.sanjijiksong.domain.openapi.dto.ProductPriceResponse;
import hekireki.sanjijiksong.domain.openapi.service.PriceSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/openapi/search")
public class OpenAPISearchController {

    private final PriceSearchService priceSearchService;

    @GetMapping("/prices")
    public ResponseEntity<List<PriceDailyDocument>> searchByItemName(
            @RequestParam("keyword") String keyword) {
        List<PriceDailyDocument> results = priceSearchService.searchByItemName(keyword);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/detail")
    public List<ProductPriceResponse> searchPriceDetail(
            @RequestParam("keyword") String keyword,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return priceSearchService.searchPriceInfoByKeyword(keyword, startDate, endDate);
    }

    @GetMapping("/autocomplete")
    public ResponseEntity<List<String>> autoComplete(@RequestParam("prefix") String prefix) throws IOException {
        List<String> suggestions = priceSearchService.autoCompleteItemNames(prefix);
        return ResponseEntity.ok(suggestions);
    }
}
