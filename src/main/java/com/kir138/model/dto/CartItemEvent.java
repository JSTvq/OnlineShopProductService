package com.kir138.model.dto;

import lombok.*;

import java.util.Objects;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartItemEvent {
    private Long cartId;
    private Long productId;
    private Integer quantity;
    private Long userId;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CartItemEvent that = (CartItemEvent) o;
        return Objects.equals(cartId, that.cartId) && Objects.equals(productId, that.productId) && Objects.equals(quantity, that.quantity) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cartId, productId, quantity, userId);
    }
}