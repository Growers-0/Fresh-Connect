package hekireki.sanjijiksong.domain.store.document;

import hekireki.sanjijiksong.domain.store.entity.Store;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "stores")
public class StoreDocument {

    @Id
    private Long id;

    @Field(type = FieldType.Text, analyzer = "standard", searchAnalyzer = "standard")
    private String name;

    @Field(type = FieldType.Text, analyzer = "standard", searchAnalyzer = "standard")
    private String address;

    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Text)
    private String image;

    @Field(type = FieldType.Boolean)
    private Boolean active;

    // Store 엔티티로부터 Document 생성
    public static StoreDocument fromEntity(Store store) {
        return StoreDocument.builder()
                .id(store.getId())
                .name(store.getName())
                .address(store.getAddress())
                .description(store.getDescription())
                .image(store.getImage())
                .active(store.getActive())
                .build();
    }
} 