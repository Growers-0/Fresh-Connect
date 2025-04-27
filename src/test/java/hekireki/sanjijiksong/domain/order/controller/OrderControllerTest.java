package hekireki.sanjijiksong.domain.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hekireki.sanjijiksong.domain.item.repository.ItemRepository;
import hekireki.sanjijiksong.domain.order.dto.OrderListUpdateRequest;
import hekireki.sanjijiksong.domain.order.dto.OrderRequest;
import hekireki.sanjijiksong.domain.order.dto.OrderResponse;
import hekireki.sanjijiksong.domain.order.entity.OrderStatus;
import hekireki.sanjijiksong.domain.order.service.OrderService;
import hekireki.sanjijiksong.domain.user.entity.Role;
import hekireki.sanjijiksong.domain.user.entity.User;
import hekireki.sanjijiksong.global.security.dto.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = OrderController.class)
@ActiveProfiles("test")
@MockBean(JpaMetamodelMappingContext.class)
class OrderControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    
    @MockBean private OrderService orderService;
    @MockBean private ElasticsearchOperations elasticsearchOperations;

    private User testUser;
    private Long itemId;
    private Long orderId;
    private OrderResponse sampleOrderResponse;

    @BeforeEach
    void setUp() {
        // 테스트 유저 설정
        testUser = User.builder()
                .id(1L)
                .email("test@aaa.com")
                .password("1234")
                .nickname("nickname")
                .role(Role.BUYER)
                .address("대한민국")
                .build();

        // 테스트 아이템 ID 설정
        itemId = 1L;
        
        // 테스트 주문 ID 설정
        orderId = 1L;
        
        // 샘플 OrderResponse 생성
        List<OrderResponse.OrderListResponse> orderLists = new ArrayList<>();
        orderLists.add(new OrderResponse.OrderListResponse(
                itemId, "상품", 10000, 2, 20000, 1L, "가게"
        ));
        
        sampleOrderResponse = new OrderResponse(
                orderId, OrderStatus.ORDERED, 2, 20000, orderLists
        );
        
        // 서비스 메소드에 대한 모의 응답 설정
        when(orderService.createOrder(any(User.class), any(OrderRequest.class)))
                .thenReturn(sampleOrderResponse);
        
        when(orderService.getOrderDetail(eq(orderId), any(User.class)))
                .thenReturn(sampleOrderResponse);
                
        when(orderService.updateOrderItems(any(OrderListUpdateRequest.class), any(User.class)))
                .thenReturn(sampleOrderResponse);
                
        when(orderService.getMyOrders(any(User.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(sampleOrderResponse)));
    }

    @Test
    @WithMockUser(roles = "BUYER")
    @DisplayName("주문 생성 테스트")
    void 주문_생성_테스트() throws Exception {
        OrderRequest orderRequest = new OrderRequest(List.of(
                new OrderRequest.OrderListRequest(itemId, 2)
        ));

        CustomUserDetails userDetails = new CustomUserDetails(testUser);

        mockMvc.perform(post("/api/v1/orders")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "BUYER")
    @DisplayName("주문 항목 수정 테스트")
    void 주문_항목_수정_테스트() throws Exception {
        // 주문 항목 수정 요청
        OrderListUpdateRequest request = new OrderListUpdateRequest(orderId, List.of(
                new OrderListUpdateRequest.OrderListItemUpdate(itemId, 3)
        ));

        CustomUserDetails userDetails = new CustomUserDetails(testUser);

        mockMvc.perform(patch("/api/v1/orders/{orderId}/items/{itemId}", orderId, itemId)
                        .with(SecurityMockMvcRequestPostProcessors.authentication(
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "BUYER")
    @DisplayName("주문 취소 테스트")
    void 주문_취소_테스트() throws Exception {
        CustomUserDetails userDetails = new CustomUserDetails(testUser);
        
        mockMvc.perform(patch("/api/v1/orders/{orderId}/cancel", orderId)
                        .with(SecurityMockMvcRequestPostProcessors.authentication(
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "BUYER")
    @DisplayName("단일 주문 조회 테스트")
    void 단일_주문_조회_테스트() throws Exception {
        CustomUserDetails userDetails = new CustomUserDetails(testUser);
        
        mockMvc.perform(get("/api/v1/orders/{orderId}", orderId)
                        .with(SecurityMockMvcRequestPostProcessors.authentication(
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()))))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "BUYER")
    @DisplayName("내 주문 목록 조회 테스트")
    void 내_주문_목록_조회_테스트() throws Exception {
        CustomUserDetails userDetails = new CustomUserDetails(testUser);
        
        mockMvc.perform(get("/api/v1/orders")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()))))
                .andExpect(status().isOk());
    }
}
