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
            "WHERE (c.sender.email = :email1 AND c.receiver.email = :email2) " +
            "OR (c.receiver.email = :email2 AND c.sender.email = :email1)")
    Optional<Chat> findByUserEmails(@Param("email1") String email1, @Param("email2") String email2);

}
