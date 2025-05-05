package hekireki.sanjijiksong.domain.chating.service;

import hekireki.sanjijiksong.domain.chating.dto.ChatListResponse;
import hekireki.sanjijiksong.domain.chating.dto.ChatMessage;
import hekireki.sanjijiksong.domain.chating.dto.MessageHistoryDTO;
import hekireki.sanjijiksong.domain.chating.dto.MessageSaveDTO;
import hekireki.sanjijiksong.domain.chating.entity.Chat;
import hekireki.sanjijiksong.domain.chating.entity.Message;
import hekireki.sanjijiksong.domain.chating.repository.ChatRepository;
import hekireki.sanjijiksong.domain.chating.repository.MessageRepository;
import hekireki.sanjijiksong.domain.user.entity.User;
import hekireki.sanjijiksong.domain.user.repository.UserRepository;
import hekireki.sanjijiksong.global.common.exception.UserException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final SimpMessageSendingOperations messagingTemplate;

public String createChatRoom(User sender, String receiverEmail) {
        Chat existingRoom = chatRepository.findByUserUidAndReceiverEmail(sender.getUid(), receiverEmail);

        if (existingRoom != null) {
            return existingRoom.getId().toString();
        }

        User receiver = userRepository.findByEmail(receiverEmail).orElseThrow(UserException.UserNotFoundException::new);

        Chat newRoom = new Chat(sender,receiver);

        return chatRepository.save(newRoom).getId().toString();
    }

    public List<MessageHistoryDTO> getChatHistory(String email, String roomId) {
        return messageRepository.findAllByChatId(UUID.fromString(roomId));

    }

    public void saveMessage(ChatMessage chatMessage, User sender) {

        MessageSaveDTO dto = messageRepository.findUserAndChatForMessage(
                sender.getUid(),
                UUID.fromString(chatMessage.chatId()))
                .orElseThrow(EntityNotFoundException::new);

        Message message = new Message(chatMessage, dto.getChat(), dto.getSender());

        messageRepository.save(message);
    }

    public void handleChatMessage(ChatMessage message, String senderEmail) {
        User sender = userRepository.findByEmail(senderEmail).orElseThrow(EntityNotFoundException::new);
        User receiver = userRepository.findByEmail(message.receiver()).orElseThrow(EntityNotFoundException::new);

        String receiverEmail = message.receiver();

        ChatMessage newMessage = new ChatMessage(
                message.type(),
                message.chatId(),
                sender.getNickname(),
                receiverEmail,
                message.message()
        );

        saveMessage(newMessage, sender);

        String destination = "/queue/messages";

        log.info("message: {}", newMessage.message());

        messagingTemplate.convertAndSendToUser(
                receiver.getEmail(),
                destination,
                newMessage
        );

        messagingTemplate.convertAndSendToUser(
                senderEmail,
                destination,
                newMessage
        );
    }

    public List<ChatListResponse> getChatList(User user) {
        return chatRepository.findByUserId(user.getId());
    }

}
