package hekireki.sanjijiksong.domain.chating.repository;

import hekireki.sanjijiksong.domain.chating.dto.MessageHistoryDTO;
import hekireki.sanjijiksong.domain.chating.dto.MessageSaveDto;
import hekireki.sanjijiksong.domain.chating.entity.Message;
import hekireki.sanjijiksong.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT new hekireki.sanjijiksong.domain.chating.dto.MessageSaveDto(u, c) " +
            "FROM User u, Chat c " +
            "WHERE u.email = :senderEmail " +
            "AND c.id = :chatId")
    Optional<MessageSaveDto> findUserAndChatForMessage(
            @Param("senderEmail") String senderEmail,
            @Param("chatId") UUID chatId
    );

    @Query("SELECT new hekireki.sanjijiksong.domain.chating.dto.MessageHistoryDTO(m.sender.email, m.message, m.createdAt) " +
            "FROM Message m " +
            "WHERE m.chat.id = :roomId " +
            "ORDER BY m.createdAt")
    List<MessageHistoryDTO> findAllByChatId(@Param("roomId") UUID roomId);}
