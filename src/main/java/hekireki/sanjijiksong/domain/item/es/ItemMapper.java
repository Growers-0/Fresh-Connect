package hekireki.sanjijiksong.domain.item.es;

import hekireki.sanjijiksong.domain.item.entity.Item;

public class ItemMapper {

    public static ItemDocument toDocument(Item item) {
        return ItemDocument.builder()
                .id("item-" + item.getId())
                .itemId(item.getId())
                .storeId(item.getStore().getId())
                .name(item.getName())
                .price(item.getPrice())
                .image(item.getImage())
                .stock(item.getStock())
                .description(item.getDescription())
                .active(item.getActive())
                .itemStatus(item.getItemStatus().name())
                .category(item.getCategory())
                .createdAt(item.getCreatedAt())
                .build();
    }
}