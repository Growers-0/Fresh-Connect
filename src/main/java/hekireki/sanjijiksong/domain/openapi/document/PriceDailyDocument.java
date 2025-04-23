package hekireki.sanjijiksong.domain.openapi.document;

import hekireki.sanjijiksong.domain.openapi.entity.PriceDaily;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;

@Document(indexName = "pricedaily")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceDailyDocument {

    @Id
    private String id; // Elasticsearch에서 ID는 String으로 처리하는 게 일반적

    @Field(type = FieldType.Keyword)
    private String categoryCode;

    @Field(type = FieldType.Keyword)
    private String classCode;

    @Field(type = FieldType.Text, analyzer = "standard") // 한글이면 nori 분석기 추천
    private String itemName;

    @Field(type = FieldType.Keyword)
    private String itemCode;

    @Field(type = FieldType.Text)
    private String kindName;

    @Field(type = FieldType.Keyword)
    private String kindCode;

    @Field(type = FieldType.Keyword)
    private String rank;

    @Field(type = FieldType.Keyword)
    private String rankCode;

    @Field(type = FieldType.Keyword)
    private String unit;

    @Field(type = FieldType.Date)
    private LocalDate snapshotDate;

    @Field(type = FieldType.Integer)
    private Integer price;

    // PriceDaily 엔티티를 PriceDailyDocument로 변환
    public static PriceDailyDocument from(PriceDaily priceDaily) {
        return PriceDailyDocument.builder()
                .categoryCode(priceDaily.getCategoryCode())
                .classCode(priceDaily.getClassCode())
                .itemName(priceDaily.getItemName())
                .itemCode(priceDaily.getItemCode())
                .kindName(priceDaily.getKindName())
                .kindCode(priceDaily.getKindCode())
                .rank(priceDaily.getRank())
                .rankCode(priceDaily.getRankCode())
                .unit(priceDaily.getUnit())
                .snapshotDate(priceDaily.getSnapshotDate())
                .price(priceDaily.getPrice())
                .build();
    }
}
