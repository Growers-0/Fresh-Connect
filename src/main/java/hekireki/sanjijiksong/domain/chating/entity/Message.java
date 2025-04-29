package hekireki.sanjijiksong.domain.chating.entity;

import hekireki.sanjijiksong.domain.chating.DTO.MessageType;
import hekireki.sanjijiksong.domain.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
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
}
