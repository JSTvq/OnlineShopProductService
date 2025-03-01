package com.kir138.kafka.hanlder;

import com.kir138.enumStatus.OutboxStatus;
import com.kir138.model.dto.CartItemEvent;
import com.kir138.model.dto.ProductValidationResponse;
import com.kir138.model.entity.OutboxProduct;
import com.kir138.reposity.OutboxProductRepository;
import com.kir138.reposity.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AddCartItemHandler {
    private final ProductRepository productRepository;
    private final OutboxProductRepository outboxProductRepository;

    public void handle(ProductValidationResponse event) {

        boolean productExists = checkProductExists(event.getProductId());
        try {
            OutboxProduct outboxProductEvent = OutboxProduct.builder()
                    .aggregateType("Cart")
                    .aggregateId(event.getCartId())
                    .type("ProductValidationResponse")
                    .topic("product-validation-response")
                    .status(OutboxStatus.PENDING)
                    .payload(ProductValidationResponse.builder()
                            .cartId(event.getCartId())
                            .productId(event.getProductId())
                            .userId(event.getUserId())
                            .quantity(event.getQuantity())
                            .isValid(productExists)
                            .build())
                    .build();
            outboxProductRepository.save(outboxProductEvent);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize ProductValidationResponse", e);
        }
    }

    private boolean checkProductExists(Long productId) {
        // Делаем запрос к БД или другому сервису, проверяем наличие товара
        return productRepository.findById(productId).isPresent();
    }
}
