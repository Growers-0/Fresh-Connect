package hekireki.sanjijiksong.domain.chating.dto;

public record ChatMessage (
        MessageType type,
        String chatId,
        String sender,
        String receiver,
        String message) {
}
