package hekireki.sanjijiksong.domain.chating.entity;

import hekireki.sanjijiksong.domain.chating.dto.ChatMessage;
import hekireki.sanjijiksong.domain.chating.dto.MessageType;
import hekireki.sanjijiksong.domain.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@RequiredArgsConstructor
public class Message {

    @Id
    private UUID id;

    private String message;
    private LocalDateTime createdAt;
    private MessageType messageType;
    
    @ManyToOne
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    public Message(ChatMessage chatMessage,Chat chat, User sender) {
        this.id = UUID.randomUUID();
        this.message = chatMessage.message();
        this.createdAt = LocalDateTime.now();
        this.messageType = chatMessage.type();
        this.sender = sender;
        this.chat = chat;
    }
}
