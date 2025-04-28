package hekireki.sanjijiksong.domain.item.es;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Document(indexName = "items")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDocument {

    @Id
    private String id; // Elasticsearch는 보통 문자열 ID

    private Long itemId;
    private Long storeId;
    private String name;
    private Integer price;
    private String image;
    private Integer stock;
    private String description;
    private Boolean active;
    private String itemStatus;
    private String category;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    private LocalDateTime createdAt;
}