package hekireki.sanjijiksong.domain.chating.entity;

import hekireki.sanjijiksong.domain.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@RequiredArgsConstructor
public class Chat {
    @Id
    @Getter
    private UUID id;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime senderLastReadTime;
    private LocalDateTime receiverLastReadTime;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private User receiver;

    public Chat(User sender, User receiver){
        this.id = UUID.randomUUID();
        this.sender = sender;
        this.receiver = receiver;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.senderLastReadTime = LocalDateTime.now();
        this.receiverLastReadTime = LocalDateTime.now();

    }
}
