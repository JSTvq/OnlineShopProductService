package com.kir138.service;

import com.kir138.mapper.ProductMapper;
import com.kir138.model.dto.ProductDto;
import com.kir138.model.entity.Product;
import com.kir138.reposity.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    public List<ProductDto> getByCategory(String category) {
        return productRepository.findByCategory(category)
                .stream()
                .map(productMapper::toMapper)
                .toList();
    }

    public List<ProductDto> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toMapper)
                .toList();
    }

    public ProductDto saveOrUpdateProduct(ProductDto productDto) {
        Product product = productMapper.toEntity(productDto);

        if (product.getId() != null) {
            return productRepository.findById(product.getId())
                    .map(existing -> {
                        existing.setName(product.getName());
                        existing.setPrice(product.getPrice());
                        existing.setCategory(product.getCategory());
                        existing.setStockQuantity(product.getStockQuantity());
                        existing.setUpdatedAt(LocalDateTime.now());
                        return productMapper.toMapper(productRepository.save(existing));
                    })
                    .orElseGet(() -> {
                        return productMapper.toMapper(productRepository.save(product));
                    });
        } else {
            Product newProduct = Product.builder()
                    .name(product.getName())
                    .price(product.getPrice())
                    .category(product.getCategory())
                    .stockQuantity(product.getStockQuantity())
                    .createdAt(LocalDateTime.now())
                    .build();
            return productMapper.toMapper(productRepository.save(newProduct));
        }
    }

    public void deleteProductById(Long id) {
        productRepository.deleteById(id);
    }

    @Transactional
    public boolean isQuantityAvailable(Long productId, Integer quantity) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));

        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("нет такого количества");
        } else {
            product.setStockQuantity(product.getStockQuantity() - quantity);
            return true;
        }
    }
}
