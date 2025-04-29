package hekireki.sanjijiksong.domain.chating.service;

import hekireki.sanjijiksong.domain.chating.dto.ChatMessage;
import hekireki.sanjijiksong.domain.chating.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository messageRepository;

//    public ChatMessage saveMessage(ChatMessage message) {
//        return messageRepository.save(message);
//    }
}
