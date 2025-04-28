package hekireki.sanjijiksong.domain.item.service;

import hekireki.sanjijiksong.domain.item.entity.Item;
import hekireki.sanjijiksong.domain.item.es.ItemDocument;
import hekireki.sanjijiksong.domain.item.es.ItemMapper;
import hekireki.sanjijiksong.domain.item.repository.ItemRepository;
import hekireki.sanjijiksong.domain.item.repository.ItemSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemSyncService {

    private final ItemRepository itemRepository;
    private final ItemSearchRepository itemSearchRepository;

    public void syncAllItemsToElasticsearch() {
        List<Item> items = itemRepository.findAll();
        List<ItemDocument> docs = items.stream()
                .map(ItemMapper::toDocument)
                .toList();
        itemSearchRepository.saveAll(docs);
    }
}
