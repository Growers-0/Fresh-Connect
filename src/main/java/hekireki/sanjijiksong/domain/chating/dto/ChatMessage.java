package hekireki.sanjijiksong.domain.chating.dto;

public record ChatMessage (
        MessageType type,
        String roomId,
        String sender,
        String receiver,
        String message) {
}
