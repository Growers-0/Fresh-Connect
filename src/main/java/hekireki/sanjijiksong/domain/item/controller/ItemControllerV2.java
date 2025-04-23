package hekireki.sanjijiksong.domain.item.controller;

import hekireki.sanjijiksong.domain.item.service.ItemService;
import hekireki.sanjijiksong.domain.item.service.ItemServiceV2;
import hekireki.sanjijiksong.global.security.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/stores")
public class ItemControllerV2 {

    private final ItemServiceV2 itemService;

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
}
