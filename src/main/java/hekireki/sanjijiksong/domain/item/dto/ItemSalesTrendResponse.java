package hekireki.sanjijiksong.domain.item.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class ItemSalesTrendResponse {

    private String title;
    private List<String> xAxisData;
    private List<Series> series;

    public ItemSalesTrendResponse(String title, List<String> xAxisData, List<Series> series) {
        this.title = title;
        this.xAxisData = xAxisData;
        this.series = series;
    }

}
