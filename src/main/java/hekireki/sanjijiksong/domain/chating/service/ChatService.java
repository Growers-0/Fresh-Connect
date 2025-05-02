package hekireki.sanjijiksong.domain.chating.service;

import hekireki.sanjijiksong.domain.chating.dto.ChatMessage;
import hekireki.sanjijiksong.domain.chating.dto.MessageHistoryDTO;
import hekireki.sanjijiksong.domain.chating.dto.MessageSaveDto;
import hekireki.sanjijiksong.domain.chating.entity.Chat;
import hekireki.sanjijiksong.domain.chating.entity.Message;
import hekireki.sanjijiksong.domain.chating.repository.ChatRepository;
import hekireki.sanjijiksong.domain.chating.repository.MessageRepository;
import hekireki.sanjijiksong.domain.user.entity.User;
import hekireki.sanjijiksong.domain.user.repository.UserRepository;
import hekireki.sanjijiksong.domain.user.service.UserService;
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

public String createChatRoom(UUID senderUid, UUID receiverUid) {
        Chat existingRoom = chatRepository.findByUserEmails(senderUid, receiverUid).orElseThrow(EntityNotFoundException::new);

        if (existingRoom != null) {
            return existingRoom.getId().toString();
        }

        User sender = userRepository.findByUid(senderUid).orElseThrow(UserException.UserNotFoundException::new);
        User receiver = userRepository.findByUid(receiverUid).orElseThrow(UserException.UserNotFoundException::new);


        Chat newRoom = new Chat(sender,receiver);

        return chatRepository.save(newRoom).getId().toString();
    }

    public List<MessageHistoryDTO> getChatHistory(String email, String roomId) {
        return messageRepository.findAllByChatId(UUID.fromString(roomId));

    }

    public void saveMessage(ChatMessage chatMessage, UUID senderUid, UUID receiverUid) {
        //TODO: chatID처리 어케함?
        MessageSaveDto dto = messageRepository.findUserAndChatForMessage(
                senderUid,
                UUID.fromString(chatMessage.chatId()))
                .orElseThrow(EntityNotFoundException::new);

        Message message = new Message(chatMessage, dto.getChat(), dto.getSender());

        messageRepository.save(message);
    }

    public void handleChatMessage(ChatMessage message, String senderEmail) {
        User sender = userRepository.findByEmail(senderEmail).orElseThrow(EntityNotFoundException::new);
        User receiver = userRepository.findByUid(UUID.fromString(message.receiver())).orElseThrow(UserException.UserNotFoundException::new);
        String receiverUid = message.receiver();

        ChatMessage newMessage = new ChatMessage(
                message.type(),
                message.chatId(),
                sender.getUid().toString(),
                receiverUid,
                message.message()
        );

//        saveMessage(newMessage, sender.getUid(),UUID.fromString(message.receiver()));

        String destination = "/queue/messages";

        log.info("message: {}", newMessage.message());

        messagingTemplate.convertAndSendToUser(
                receiver.getEmail(),
                destination,
                message
        );

        messagingTemplate.convertAndSendToUser(
                senderEmail,
                destination,
                newMessage.message()
        );
    }

    public UUID getUidForTest(){
        return userRepository.findById(5L).orElseThrow(UserException.UserNotFoundException::new).getUid();
    }
}
