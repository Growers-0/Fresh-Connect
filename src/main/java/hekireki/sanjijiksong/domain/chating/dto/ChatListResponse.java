package hekireki.sanjijiksong.domain.chating.dto;

import hekireki.sanjijiksong.domain.chating.entity.Chat;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class ChatListResponse {
    UUID chatId;
    String receiver;
    LocalDateTime myLastReadTime;
    LocalDateTime receiverLastReadTime;

    public ChatListResponse(Chat chat) {
        this.chatId = chat.getId();
        this.receiver = chat.getReceiver().getNickname();
        this.myLastReadTime = chat.getSenderLastReadTime();
        this.receiverLastReadTime = chat.getSenderLastReadTime();
    }

    public ChatListResponse(UUID chatId, String receiver, LocalDateTime myLastReadTime, LocalDateTime receiverLastReadTime) {
        this.chatId = chatId;
        this.receiver = receiver;
        this.myLastReadTime = myLastReadTime;
        this.receiverLastReadTime = receiverLastReadTime;
    }
}
