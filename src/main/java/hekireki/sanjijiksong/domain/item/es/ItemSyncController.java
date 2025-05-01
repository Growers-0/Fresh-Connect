package hekireki.sanjijiksong.domain.item.es;

import hekireki.sanjijiksong.domain.item.service.ItemSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/item/sync")
@RequiredArgsConstructor
public class ItemSyncController {

    private final ItemSyncService itemSyncService;

    @PostMapping
    public ResponseEntity<String> syncPricesToElasticsearch() {
        itemSyncService.syncAllItemsToElasticsearch();

        return ResponseEntity.ok("아이템 동기화 완료");
    }
}
