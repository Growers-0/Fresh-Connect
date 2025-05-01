package hekireki.sanjijiksong.domain.chating.controller;

import hekireki.sanjijiksong.domain.chating.dto.ChatMessage;
import hekireki.sanjijiksong.domain.chating.dto.MessageHistoryDTO;
import hekireki.sanjijiksong.domain.chating.service.ChatService;
import hekireki.sanjijiksong.global.security.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    @GetMapping("/chat/test")
    public String chatTest() {
        return "chatTest";

    }

    // 채팅방 생성
    @PostMapping("/chat/room")
    @ResponseBody
    public String createRoom(@RequestParam String receiverEmail, Principal principal) {
        return chatService.createChatRoom(principal.getName(), receiverEmail);
    }

    // 채팅 히스토리 조회
    @GetMapping("/chat/history/{roomId}")
    @ResponseBody //TODO: 유저 검증 추가
    public ResponseEntity<List<MessageHistoryDTO>> getChatHistory(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable String roomId) {
        return ResponseEntity.ok(chatService.getChatHistory(customUserDetails.getUsername(),roomId));
    }

    @MessageMapping("/chat/message")
    public void sendMessage(ChatMessage message, Principal principal) {
        String senderEmail = principal.getName();
        log.info("Received message: {}", message);

        String destination = "/queue/messages"; // 구독 경로

        // 메시지 보내는 사람 설정
        ChatMessage newMessage = new ChatMessage(
                message.type(),
                message.chatId(),
                senderEmail,
                message.receiver(),
                message.message()
        );

        //메시지 저장
        chatService.saveMessage(newMessage, senderEmail, message.receiver());

        // 받는 사람의 구독 주소로 메시지 전송
        messagingTemplate.convertAndSendToUser(
                newMessage.receiver(),           // 받는 사람의 이메일
                destination,            // 구독 엔드포인트
                message                       // 보낼 메시지
        );

        // 보낸 사람에게 메시지 전송
        messagingTemplate.convertAndSendToUser(
                senderEmail,
                destination,
                newMessage
        );
    }
}
