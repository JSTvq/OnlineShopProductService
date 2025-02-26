package com.kir138.service;

import com.kir138.model.dto.ProductValidationResponse;
import com.kir138.enumStatus.OutboxStatus;
import com.kir138.model.entity.OutboxProduct;
import com.kir138.reposity.OutboxProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductOutboxProcessor {
    private final OutboxProductRepository outboxProductRepository;
    private final KafkaTemplate<String, ProductValidationResponse> kafkaTemplate;

    @Scheduled(fixedDelay = 5000)
    public void processPendingEvents() {
        List<OutboxProduct> products = outboxProductRepository.findAllByStatus(OutboxStatus.PENDING);
        for (OutboxProduct product : products) {
            try {
                // Отправка в Kafka
                kafkaTemplate.send(product.getTopic(), product.getPayload());
                // Если отправка прошла успешно, меняем статус на SENT.
                product.setStatus(OutboxStatus.SENT);
                outboxProductRepository.save(product);
            } catch (Exception ex) {
                product.setStatus(OutboxStatus.FAILED);
                outboxProductRepository.save(product);
            }
        }
    }
}