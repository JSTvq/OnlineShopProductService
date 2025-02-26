package com.kir138.kafka.listener;

import com.kir138.model.dto.CartItemEvent;
import com.kir138.kafka.hanlder.AddCartItemHandler;
import com.kir138.model.dto.ProductValidationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import static org.springframework.kafka.listener.ContainerProperties.AckMode.MANUAL;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceConsumer {
    private final AddCartItemHandler addCartItemHandler;

    @KafkaListener(topics = "cart-item-added", groupId = "online-shop-group")
    public void listenCartItemEvent(ConsumerRecord<String, ProductValidationResponse> consumerRecord) {
        try {
            ProductValidationResponse event = consumerRecord.value();
            addCartItemHandler.handle(event);

        } catch (RuntimeException e) {
            log.error(e.getMessage());
        }
    }
}
