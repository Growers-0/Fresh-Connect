package hekireki.sanjijiksong.domain.chating.controller;

import hekireki.sanjijiksong.domain.chating.DTO.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping("/chattest")
    public String chattest() {
        return "chatTest.html";
    }

    @MessageMapping("/chat/message")
    public void sendMessage(ChatMessage message, Principal principal) {
        String senderEmail = principal.getName();
        log.info("Received message: {}", message);

        String destination = "/queue/messages"; // 구독 경로

        // 메시지 보내는 사람 설정
        message = new ChatMessage(
                message.type(),
                message.roomId(),
                senderEmail,
                message.receiver(),
                message.message()
        );

        // 받는 사람의 구독 주소로 메시지 전송
        messagingTemplate.convertAndSendToUser(
                message.receiver(),           // 받는 사람의 이메일
                destination,            // 구독 엔드포인트
                message                       // 보낼 메시지
        );

        // 보낸 사람에게도 메시지 전송 (본인 화면에도 표시하기 위해)
        messagingTemplate.convertAndSendToUser(
                senderEmail,
                destination,
                message
        );
    }
}
