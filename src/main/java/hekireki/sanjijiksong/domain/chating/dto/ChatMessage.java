package hekireki.sanjijiksong.domain.chating.dto;

import lombok.Getter;

import java.util.UUID;


public record ChatMessage (
        MessageType type,
        String chatId,
        String sender,
        String receiver,
        String message) {
}
