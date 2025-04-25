package hekireki.sanjijiksong.domain.item.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class Series {
    private String name;
    private List<?> data;

    public Series(String name, List<Long> data) {
        this.name = name;
        this.data = data;
    }
}