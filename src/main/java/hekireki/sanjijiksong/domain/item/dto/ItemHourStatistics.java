package hekireki.sanjijiksong.domain.item.dto;

import lombok.Getter;

@Getter
public class ItemHourStatistics {
    private final int hour;
    private final long revenue;

    public ItemHourStatistics(Number hour, Number revenue) {
        this.hour = hour.intValue();
        this.revenue = revenue.longValue();
    }
}
