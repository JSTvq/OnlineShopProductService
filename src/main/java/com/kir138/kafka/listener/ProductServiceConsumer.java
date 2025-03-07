package com.kir138.kafka.listener;

import com.kir138.kafka.hanlder.AddCartItemHandler;
import com.kir138.model.dto.ProductValidationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceConsumer {

    private final AddCartItemHandler addCartItemHandler;

    @KafkaListener(topics = "cart-item-added", groupId = "online-shop-group1")
    public void listenCartItemEvent(ConsumerRecord<String, ProductValidationResponse>
                                                consumerRecord,
                                                Acknowledgment ack) {
        try {
            ProductValidationResponse response = consumerRecord.value();
            System.out.println("Получено сообщение от Кафка: " + response);
            addCartItemHandler.handle(response);
            ack.acknowledge();
        } catch (RuntimeException e) {
            log.error(e.getMessage());
        }
    }
}
