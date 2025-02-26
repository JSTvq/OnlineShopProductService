package com.kir138.service;

import com.kir138.mapper.ProductMapper;
import com.kir138.model.dto.ProductDto;
import com.kir138.model.entity.Product;
import com.kir138.reposity.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    public final ProductRepository productRepository;
    public final ProductMapper productMapper;

    //Добавьте логику установки значений полей createdAt, updatedAt

    public ProductDto getProductById(Long id) {
        return productRepository.findById(id)
                .stream()
                .map(productMapper::toMapper)
                .findFirst()
                .orElseThrow();
    }

    public ProductDto getByCategory(String category) {
        return productRepository.findByCategory(category)
                .stream()
                .map(productMapper::toMapper)
                .findFirst()
                .orElseThrow();
    }

    public List<ProductDto> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toMapper)
                .toList();
    }

    public ProductDto saveOrUpdateProduct(Product product) {
        Product savedProduct = productRepository.findById(product.getId())
                .map(existingProduct -> {
                    existingProduct.setName(product.getName());
                    existingProduct.setPrice(product.getPrice());
                    existingProduct.setCategory(product.getCategory());
                    existingProduct.setStockQuantity(product.getStockQuantity());
                    return productRepository.save(existingProduct);
                })
                .orElseGet(() -> {
                    return productRepository.save(Product.builder()
                            .name(product.getName())
                            .price(product.getPrice())
                            .category(product.getCategory())
                            .stockQuantity(product.getStockQuantity())
                            .build());
                });
        return productMapper.toMapper(savedProduct);
    }

    public void deleteProductById(Long id) {
        productRepository.deleteById(id);
    }
}
