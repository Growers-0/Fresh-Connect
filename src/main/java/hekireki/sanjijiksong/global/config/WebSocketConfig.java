package hekireki.sanjijiksong.global.config;

import hekireki.sanjijiksong.global.common.interceptor.JwtHandshakeInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer  {

    private final JwtHandshakeInterceptor jwtHandshakeInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/chat") // 클라이언트 연결 주소
                .addInterceptors(jwtHandshakeInterceptor)
                .setAllowedOriginPatterns("*") // CORS 설정
                .withSockJS(); // (브라우저 호환성 위해 SockJS fallback 지원)
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/queue", "/topic"); // 메시지 받을 때 prefix
        registry.setApplicationDestinationPrefixes("/app"); // 클라이언트 보낼 때 prefix
    }
}
