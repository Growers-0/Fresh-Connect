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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

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
    public String createRoom(@RequestParam UUID receiverUid, @AuthenticationPrincipal CustomUserDetails customUserDetails ) {
        return chatService.createChatRoom(customUserDetails.getUid(), receiverUid);
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
        chatService.handleChatMessage(message, senderEmail);
    }

    @GetMapping("/chat/testUid")
    @ResponseBody
    public String getUserUidForTest(){
        return chatService.getUidForTest().toString();
    }
}
