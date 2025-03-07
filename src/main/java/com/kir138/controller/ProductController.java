package com.kir138.controller;

import com.kir138.mapper.ProductMapper;
import com.kir138.model.dto.ProductDto;
import com.kir138.model.entity.Product;
import com.kir138.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
@Validated
public class ProductController {

    private final ProductService productService;

    private final ProductMapper productMapper;

    public ProductDto getProductById(Long id) {
        return productService.getProductById(id);
    }

    public ProductDto getByCategory(String category) {
        return productService.getByCategory(category);
    }

    public List<ProductDto> getAllProducts() {
        return productService.getAllProducts();
    }

    @PostMapping("/saveProduct")
    public ProductDto saveOrUpdateProduct(@Validated @RequestBody ProductDto productDto) {
        Product pr = productMapper.toEntity(productDto);
        return productService.saveOrUpdateProduct(pr);
    }

    public void deleteProductById(Long id) {
        productService.deleteProductById(id);
    }
}
