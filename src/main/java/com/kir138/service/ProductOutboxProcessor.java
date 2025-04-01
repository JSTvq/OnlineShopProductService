package com.kir138.service;

import com.kir138.model.dto.ProductValidationResponse;
import com.kir138.enumStatus.OutboxStatus;
import com.kir138.model.entity.OutboxProduct;
import com.kir138.reposity.OutboxProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductOutboxProcessor {

    private final OutboxProductRepository outboxProductRepository;
    private final KafkaTemplate<String, ProductValidationResponse> kafkaTemplate;

    @Scheduled(fixedDelay = 5_000)
    public void processPendingEvents() {

        List<OutboxProduct> products;
        Pageable pageable = Pageable.ofSize(100);

        do {
            System.out.println("стартует метод с отправкой через кафку");
            products = outboxProductRepository.findAllByStatus(OutboxStatus.PENDING);
            System.out.println("выводим то что нашли " + products.toString());
            for (OutboxProduct product : products) {
                try {
                    // Отправка в Kafka
                    System.out.println("Sending message to Kafka: " + product.getPayload());
                    kafkaTemplate.send(product.getTopic(), product.getPayload()).get();
                    System.out.println("Message sent successfully, changing status to SENT");
                    // Если отправка прошла успешно, меняем статус на SENT.
                    product.setStatus(OutboxStatus.SENT);
                } catch (Exception ex) {
                    product.setStatus(OutboxStatus.FAILED);
                }
                outboxProductRepository.save(product);
            }
            pageable = pageable.next();
        } while (!products.isEmpty());
    }
}