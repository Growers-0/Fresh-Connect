package hekireki.sanjijiksong.domain.item.es;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import hekireki.sanjijiksong.domain.item.entity.Item;
import hekireki.sanjijiksong.domain.item.entity.ItemStatus;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDateTime;

@Document(indexName = "items")
@Setting(settingPath = "elasticsearch/setting.json")
@Mapping(mappingPath = "elasticsearch/item-mapping.json")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemDocument {

    @Id
    private String id; // Elasticsearch는 보통 문자열 ID

    private Long storeId;
    private String name;
    private Integer price;
    private String image;
    private Integer stock;
    private String description;
    private Boolean active;
    private ItemStatus itemStatus;
    private String category;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    private LocalDateTime createdAt;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    private LocalDateTime modifiedAt;

    public static ItemDocument toDocument(Item item) {
        return ItemDocument.builder()
                .id("item-" + item.getId())
                .storeId(item.getStore().getId())
                .name(item.getName())
                .price(item.getPrice())
                .image(item.getImage())
                .stock(item.getStock())
                .description(item.getDescription())
                .active(item.getActive())
                .itemStatus(item.getItemStatus())
                .category(item.getCategory())
                .createdAt(item.getCreatedAt())
                .build();
    }
}