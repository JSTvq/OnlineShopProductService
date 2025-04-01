package com.kir138.kafka.hanlder;

import com.kir138.enumStatus.OutboxStatus;
import com.kir138.model.dto.ProductValidationResponse;
import com.kir138.model.entity.OutboxProduct;
import com.kir138.reposity.OutboxProductRepository;
import com.kir138.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AddCartItemHandler {

    private final ProductService productService;

    private final OutboxProductRepository outboxProductRepository;

    public void handle(ProductValidationResponse event) {
        boolean productExists = checkProductExists(event.getProductId(), event.getQuantity());
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

    private boolean checkProductExists(Long productId, Integer quantity) {
        // Делаем запрос к БД или другому сервису, проверяем наличие товара
        //сделать так чтобы была проверка есть ли такой продукт вообще и если
        // есть то нужна проверка по кол-ву, есть ли такое или нет

        return productService.isQuantityAvailable(productId, quantity);
    }
}
