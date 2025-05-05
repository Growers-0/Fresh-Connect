package hekireki.sanjijiksong.domain.chating.repository;

import hekireki.sanjijiksong.domain.chating.dto.ChatListResponse;
import hekireki.sanjijiksong.domain.chating.dto.ChatMessage;
import hekireki.sanjijiksong.domain.chating.entity.Chat;
import hekireki.sanjijiksong.domain.user.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatRepository extends JpaRepository<Chat, UUID> {

    @Query("SELECT c FROM Chat c " +
            "WHERE (c.sender.uid = :senderUid AND c.receiver.email = :receiverEmail) " +
            "OR (c.receiver.uid = :senderUid AND c.sender.email = :receiverEmail)")
    Chat findByUserUidAndReceiverEmail(@Param("senderUid") UUID senderUid, @Param("receiverEmail") String receiverEmail);

    @Query("SELECT new hekireki.sanjijiksong.domain.chating.dto.ChatListResponse(" +
            "c.id," +
            "CASE WHEN c.sender.id = :id THEN c.receiver.email ELSE c.sender.email END, " +
            "CASE WHEN c.sender.id = :id THEN c.senderLastReadTime ELSE c.receiverLastReadTime END, " +
            "CASE WHEN c.sender.id = :id THEN c.receiverLastReadTime ELSE c.senderLastReadTime END" +
            ") " +
            "FROM Chat c " +
            "WHERE c.sender.id = :id OR c.receiver.id = :id")
    List<ChatListResponse> findByUserId(@Param("id") Long userId);
}
