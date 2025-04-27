package hekireki.sanjijiksong.domain.item.controller;

import hekireki.sanjijiksong.domain.item.es.ItemDocument;
import hekireki.sanjijiksong.domain.item.repository.ItemSearchRepository;
import hekireki.sanjijiksong.domain.item.service.ItemServiceV2;
import hekireki.sanjijiksong.global.security.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/stores")
public class ItemControllerV2 {

    private final ItemServiceV2 itemService;
    private final ItemSearchRepository itemSearchRepository;

    @GetMapping("/statistics")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<?> getSalesOverview(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        return itemService.getSalesOverviewV2(customUserDetails.getUsername());
    }

    @GetMapping("/best-products")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<?> getTop5BestSellingProducts(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        return itemService.getTop5BestSellingProducts(customUserDetails.getUsername());
    }

    @GetMapping("/weekly-sales")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<?> getWeeklySalesTrend(@AuthenticationPrincipal CustomUserDetails customUserDetails, int recentDays){
        return itemService.getWeeklySalesTrend(customUserDetails.getUsername(),recentDays);
    }

    @GetMapping("/hourly-sales")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<?> getDailyHourlySales(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                 @RequestParam String localDateTime // format: yyyy-mm-dd
    ){
        LocalDate localDate = LocalDate.parse(localDateTime);
        return itemService.getDailyHourlySales(customUserDetails.getUsername(),localDate);
    }

    @GetMapping("/items/search")
    public List<ItemDocument> search(@RequestParam String keyword) {
        return itemSearchRepository.findByNameContainingOrDescriptionContaining(keyword, keyword);
    }
}
