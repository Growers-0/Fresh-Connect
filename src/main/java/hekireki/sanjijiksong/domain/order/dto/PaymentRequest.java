package hekireki.sanjijiksong.domain.order.dto;

public record PaymentRequest(String orderId, String amount, String paymentKey) {

}
