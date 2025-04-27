package hekireki.sanjijiksong.domain.item.es;

import hekireki.sanjijiksong.domain.item.service.ItemSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ItemSyncRunner implements CommandLineRunner {

    private final ItemSyncService itemSyncService;

    @Override
    public void run(String... args) {
        itemSyncService.syncAllItemsToElasticsearch();
    }
}
