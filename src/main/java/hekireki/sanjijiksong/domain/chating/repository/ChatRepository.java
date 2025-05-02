package hekireki.sanjijiksong.domain.chating.repository;

import hekireki.sanjijiksong.domain.chating.dto.ChatMessage;
import hekireki.sanjijiksong.domain.chating.entity.Chat;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface ChatRepository extends JpaRepository<Chat, UUID> {
    @Query("SELECT c FROM Chat c " +
            "WHERE (c.sender.uid = :senderUid AND c.receiver.uid = :receiverUid) " +
            "OR (c.receiver.uid = :receiverUid AND c.sender.uid = :senderUid)")
    Optional<Chat> findByUserEmails(@Param("senderUid") UUID senderUid, @Param("receiverUid") UUID receiverUid);

}
