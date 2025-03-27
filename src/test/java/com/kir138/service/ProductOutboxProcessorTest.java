package com.kir138.service;

import com.kir138.BaseIntegrationTest;
import com.kir138.enumStatus.OutboxStatus;
import com.kir138.model.dto.ProductValidationResponse;
import com.kir138.model.entity.OutboxProduct;
import com.kir138.reposity.OutboxProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ProductOutboxProcessorTest extends BaseIntegrationTest {

    @Autowired
    private OutboxProductRepository outboxProductRepository;

    @Autowired
    private ProductOutboxProcessor productOutboxProcessor;

    @MockitoBean
    private KafkaTemplate<String, ProductValidationResponse> kafkaTemplate;

    @BeforeEach
    void setUp() {
        outboxProductRepository.deleteAll();
    }

    private ProductValidationResponse getProductValidationResponse() {
        return ProductValidationResponse.builder()
                .productId(1L)
                .cartId(100L)
                .userId(1000L)
                .quantity(2)
                .isValid(true)
                .build();
    }

    private OutboxProduct getOutboxProduct(ProductValidationResponse payload) {
        return OutboxProduct.builder()
                .aggregateType("Cart")
                .aggregateId(100L)
                .type("ProductValidationResponse")
                .topic("product-validation-response")
                .status(OutboxStatus.PENDING)
                .payload(ProductValidationResponse.builder()
                        .productId(payload.getProductId())
                        .cartId(payload.getCartId())
                        .userId(payload.getUserId())
                        .quantity(payload.getQuantity())
                        .isValid(true)
                        .build())
                .build();
    }

    /*@AfterEach
    void tearDown() {
    }*/

    @Test
    void testProcessPendingEvents_sent() {

        outboxProductRepository.save(getOutboxProduct(getProductValidationResponse()));

        SendResult<String, ProductValidationResponse> sendResult =
                mock(SendResult.class);

        when(kafkaTemplate.send(anyString(), any(ProductValidationResponse.class)))
                .thenReturn(CompletableFuture.completedFuture(sendResult));

        productOutboxProcessor.processPendingEvents();

        assertThat(outboxProductRepository.findAll().getFirst().getStatus())
                    .isEqualTo(OutboxStatus.SENT);
    }

    @Test
    void testProcessPendingEvents_failed() {

        outboxProductRepository.save(getOutboxProduct(getProductValidationResponse()));

        // мок ошибки отправки
        CompletableFuture<SendResult<String, ProductValidationResponse>> future =
                new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Kafka error"));

        when(kafkaTemplate.send(anyString(), any(ProductValidationResponse.class)))
                .thenReturn(future);

        productOutboxProcessor.processPendingEvents();

        assertThat(outboxProductRepository.findAll().getFirst().getStatus())
                    .isEqualTo(OutboxStatus.FAILED);
    }
}