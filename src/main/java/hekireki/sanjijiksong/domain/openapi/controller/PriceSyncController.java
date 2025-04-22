package hekireki.sanjijiksong.domain.openapi.controller;

import hekireki.sanjijiksong.domain.openapi.service.PriceDailySyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/sync")
@RequiredArgsConstructor
@Slf4j
public class PriceSyncController {

    private final PriceDailySyncService priceDailySyncService;

    @PostMapping
    public ResponseEntity<String> syncPricesToElasticsearch() {
        // 동기화 작업을 비동기적으로 실행
        priceDailySyncService.syncAllToElasticsearch();

        // 작업 시작을 알리는 응답 (실제 동기화는 비동기로 처리되므로 즉시 응답)
        return ResponseEntity.ok("동기화 작업이 시작되었습니다.");
    }
}
