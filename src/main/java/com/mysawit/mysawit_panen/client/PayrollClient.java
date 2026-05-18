package com.mysawit.mysawit_panen.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class PayrollClient {
    @Value("${pembayaran.base-url:http://localhost:8085}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    public PayrollClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void createPayroll(UUID buruhId, double kilogram) {
        String url = baseUrl + "/api/pembayaran/payroll";

        Map<String, Object> body = new HashMap<>();
        body.put("userId", buruhId.toString());
        body.put("userRole", "BURUH");
        body.put("kilogram", kilogram);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-User-Id", buruhId.toString());

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
            log.info("Payroll created for buruh {}: status={}", buruhId, response.getStatusCode());
        } catch (Exception e) {
            log.error("Failed to create payroll for buruh {}: {}", buruhId, e.getMessage());
            throw new RuntimeException("Payroll creation failed for buruh " + buruhId, e);
        }
    }
}