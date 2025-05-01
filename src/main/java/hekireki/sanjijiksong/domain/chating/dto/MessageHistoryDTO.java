package hekireki.sanjijiksong.domain.chating.dto;

import java.time.LocalDateTime;

public record MessageHistoryDTO (
        String sender,
        String message,
        LocalDateTime createdAt
){
}
