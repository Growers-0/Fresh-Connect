package hekireki.sanjijiksong.global.common.interceptor;

import hekireki.sanjijiksong.domain.user.entity.User;
import hekireki.sanjijiksong.domain.user.repository.UserRepository;
import hekireki.sanjijiksong.global.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompAuthChannelInterceptor implements ChannelInterceptor {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            log.info("Processing STOMP CONNECT command");

            // STOMP 연결 시 전달된 헤더에서 JWT 토큰 추출
            List<String> authorization = accessor.getNativeHeader("Authorization");
            log.info("Authorization header: {}", authorization);

            if (authorization != null && !authorization.isEmpty()) {
                String bearerToken = authorization.get(0);
                if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
                    String token = bearerToken.substring(7);
                    try {
                        // JWT 토큰 검증
                        if (!jwtUtil.isExpired(token)) {
                            String email = jwtUtil.getEmail(token);
                            User user = userRepository.findByEmail(email)
                                    .orElseThrow(() -> new MessagingException("User not found"));

                            // 인증 정보를 STOMP 세션에 저장
                            accessor.setUser(new Principal() {
                                @Override
                                public String getName() {
                                    return email;
                                }
                            });

                            log.info("Authentication successful for user: {}", email);
                        } else {
                            throw new MessagingException("Token is expired");
                        }
                    } catch (Exception e) {
                        log.error("Authentication failed: ", e);
                        throw new MessagingException("Authentication failed: " + e.getMessage());
                    }
                }
            } else {
                log.error("No Authorization header found");
                throw new MessagingException("No Authorization header found");
            }
        }
        return message;
    }
}

