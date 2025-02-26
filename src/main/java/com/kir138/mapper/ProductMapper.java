package com.kir138.mapper;

import com.kir138.model.dto.ProductDto;
import com.kir138.model.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductDto toMapper(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .category(product.getCategory())
                .build();
    }

    public Product toEntity(ProductDto productDto) {
        return Product.builder()
                .id(productDto.getId())
                .name(productDto.getName())
                .price(productDto.getPrice())
                .stockQuantity(productDto.getStockQuantity())
                .category(productDto.getCategory())
                .build();
    }
}
