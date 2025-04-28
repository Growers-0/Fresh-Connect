package hekireki.sanjijiksong.domain.item.dto;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ItemDateStatistics{
    private LocalDate date;
    private Long revenue;

    public ItemDateStatistics(java.sql.Date date, Number revenue) {
        this.date = date.toLocalDate();
        this.revenue = revenue.longValue();
    }
}
