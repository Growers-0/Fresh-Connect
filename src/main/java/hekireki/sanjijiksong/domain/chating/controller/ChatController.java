package hekireki.sanjijiksong.domain.chating.controller;

import hekireki.sanjijiksong.domain.chating.dto.ChatListResponse;
import hekireki.sanjijiksong.domain.chating.dto.ChatMessage;
import hekireki.sanjijiksong.domain.chating.dto.MessageHistoryDTO;
import hekireki.sanjijiksong.domain.chating.service.ChatService;
import hekireki.sanjijiksong.global.security.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/chat/test")
    public String chatTest() {
        return "chatTest";

    }

    // 채팅방 생성
    @GetMapping("/chat/room")
    @ResponseBody
    public ResponseEntity<Map<String, String>> createRoom(@RequestParam String receiverEmail, @AuthenticationPrincipal CustomUserDetails customUserDetails ) {
        System.out.println(receiverEmail);
        String chatId = chatService.createChatRoom(customUserDetails.getUser(), receiverEmail);
        return getChatId(chatId);
    }

    private static ResponseEntity<Map<String, String>> getChatId(String chatId) {
        return ResponseEntity.ok(Map.of("chatId", chatId));
    }

    // 채팅 히스토리 조회
    @GetMapping("api/v1/chat/history/{roomId}")
    @ResponseBody //TODO: 유저 검증 추가
    public ResponseEntity<List<MessageHistoryDTO>> getChatHistory(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable String roomId) {
        return ResponseEntity.ok(chatService.getChatHistory(customUserDetails.getUsername(),roomId));
    }

    @GetMapping("api/v1/chat/history")
    @ResponseBody
    public ResponseEntity<?> getChatList(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        List<ChatListResponse> chatList = chatService.getChatList(customUserDetails.getUser());

        return ResponseEntity.ok(chatList);

    }

    @MessageMapping("/chat/message")
    public void sendMessage(ChatMessage message, Principal principal) {
        String senderEmail = principal.getName();
        chatService.handleChatMessage(message, senderEmail);
    }

}
