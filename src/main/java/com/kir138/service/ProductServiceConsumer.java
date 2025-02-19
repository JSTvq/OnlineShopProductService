package com.kir138.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kir138.model.dto.CartItemEvent;
import com.kir138.model.dto.ProductValidationResponse;
import com.kir138.model.entity.Outbox;
import com.kir138.reposity.OutboxRepository;
import com.kir138.reposity.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceConsumer {
    private final ProductRepository productRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final OutboxRepository outboxRepository;

    @KafkaListener(topics = "cart-item-added", groupId = "online-shop-group")
    public void listenCartItemEvent(String message) {
        try {
            CartItemEvent event = objectMapper.readValue(message, CartItemEvent.class);
            boolean productExists = checkProductExists(event.getProductId());
            ProductValidationResponse response = ProductValidationResponse.builder()
                    .cartId(event.getCartId())
                    .productId(event.getProductId())
                    .isValid(productExists)
                    .quantity(event.getQuantity())
                    .build();

            String payload = objectMapper.writeValueAsString(response);
            Outbox outboxEvent = Outbox.builder()
                    .aggregateType("Cart")
                    .aggregateId(event.getCartId())
                    .type("ProductValidationResponse")
                    .payload(payload)
                    .build();
            outboxRepository.save(outboxEvent);

            kafkaTemplate.send("product-validation-response", payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize ProductValidationResponse", e);
        }
    }

    private boolean checkProductExists(Long productId) {
        // Делаем запрос к БД или другому сервису, проверяем наличие товара
        return productRepository.findById(productId).isPresent();
    }
}
