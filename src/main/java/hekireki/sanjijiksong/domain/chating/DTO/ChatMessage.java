package hekireki.sanjijiksong.domain.chating.DTO;

public record ChatMessage (
        MessageType type,
        String roomId,
        String sender,
        String receiver,
        String message) {
}
