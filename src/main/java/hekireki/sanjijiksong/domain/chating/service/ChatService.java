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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    public String createChatRoom(String senderEmail, String receiverEmail) {
        Chat existingRoom = chatRepository.findByUserEmails(senderEmail, receiverEmail).orElseThrow(EntityNotFoundException::new);

        if (existingRoom != null) {
            return existingRoom.getId().toString();
        }

        User sennder = userRepository.findByEmail(senderEmail).orElseThrow(UserException.UserNotFoundException::new);
        User receiver = userRepository.findByEmail(receiverEmail).orElseThrow(UserException.UserNotFoundException::new);


        Chat newRoom = new Chat(sennder,receiver);

        return chatRepository.save(newRoom).getId().toString();
    }

    public List<MessageHistoryDTO> getChatHistory(String email, String roomId) {
        return messageRepository.findAllByChatId(UUID.fromString(roomId));

    }

    public void saveMessage(ChatMessage chatMessage, String senderEmail, String receiverEmail) {

        MessageSaveDto dto = messageRepository.findUserAndChatForMessage(
                senderEmail,
                UUID.fromString(chatMessage.chatId()))
                .orElseThrow(EntityNotFoundException::new);

        Message message = new Message(chatMessage, dto.getChat(), dto.getSender());

        messageRepository.save(message);
    }
}
