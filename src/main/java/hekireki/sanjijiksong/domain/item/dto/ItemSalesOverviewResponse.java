package hekireki.sanjijiksong.domain.item.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class ItemSalesOverviewResponse{

    private String title;
    private List<String> labels;
    private List<Series> series;

    public ItemSalesOverviewResponse(String title, List<String> labels, List<Series> series) {
        this.title = title;
        this.labels = labels;
        this.series = series;
    }


}
