package com.kir138.controller;

import com.kir138.model.dto.ProductDto;
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

    @GetMapping("/{id}")
    public ProductDto getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @GetMapping("/getCategory")
    public List<ProductDto> getByCategory(@RequestParam String category) {
        return productService.getByCategory(category);
    }

    @GetMapping("/allProducts")
    public List<ProductDto> getAllProducts() {
        return productService.getAllProducts();
    }

    @PostMapping("/saveProduct")
    public ProductDto saveOrUpdateProduct(@RequestBody ProductDto productDto) {
        return productService.saveOrUpdateProduct(productDto);
    }

    @DeleteMapping("/{id}")
    public void deleteProductById(@PathVariable Long id) {
        productService.deleteProductById(id);
    }
}
