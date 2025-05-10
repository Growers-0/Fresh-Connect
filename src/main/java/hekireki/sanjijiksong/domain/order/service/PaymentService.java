package hekireki.sanjijiksong.domain.order.service;

import hekireki.sanjijiksong.domain.order.dto.PaymentRequest;
import hekireki.sanjijiksong.global.common.exception.ErrorCode;
import hekireki.sanjijiksong.global.common.exception.OrderException;
import java.util.Base64;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class PaymentService {

    @Value("${tosspayments.url.confirm}")
    private String confirmUrl;

    @Value("${tosspayments.secret}")
    private String secretKey;

    @Transactional
    public Map<String, Object> confirm(PaymentRequest paymentRequest) {
        String auth = Base64.getEncoder().encodeToString((secretKey + ":").getBytes());

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Basic " + auth);

        HttpEntity<PaymentRequest> entity = new HttpEntity<>(paymentRequest, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(confirmUrl, entity, Map.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new OrderException(ErrorCode.PAYMENT_SERVER_INTERVAL);
        }

        return response.getBody();
    }
}
