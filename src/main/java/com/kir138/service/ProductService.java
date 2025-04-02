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

    @Transactional
    public ProductDto saveOrUpdateProduct(ProductDto productDto) {
        Product product = productMapper.toEntity(productDto);

        // Проверяем, существует ли продукт с таким названием
        Product existingProduct = productRepository.findByName(product.getName());

        if (existingProduct != null) {
            // Обновляем существующий продукт
            existingProduct.setName(product.getName());
            existingProduct.setPrice(product.getPrice());
            existingProduct.setCategory(product.getCategory());
            existingProduct.setStockQuantity(product.getStockQuantity());
            return productMapper.toMapper(productRepository.save(existingProduct));
        } else {
            // Создаем новый продукт
            return productMapper.toMapper(productRepository.save(product));
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
