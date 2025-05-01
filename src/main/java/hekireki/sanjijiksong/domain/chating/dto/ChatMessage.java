package hekireki.sanjijiksong.domain.chating.dto;

import lombok.Getter;


public record ChatMessage (
        MessageType type,
        String chatId,
        String sender,
        String receiver,
        String message) {
}
