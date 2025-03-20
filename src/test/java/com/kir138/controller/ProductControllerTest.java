package com.kir138.controller;

import com.kir138.BaseIntegrationTest;
import com.kir138.model.dto.ProductDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebClient
class ProductControllerTest extends BaseIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private ProductDto testProduct;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        testProduct = ProductDto.builder()
                .price(BigDecimal.valueOf(100,33))
                .category("категория1")
                .stockQuantity(66)
                .name("товар1")
                .build();
    }

    @Test
    void testCreateAndGetProduct() {
        ResponseEntity<ProductDto> response = restTemplate.postForEntity(
                "http://localhost:" + port + "api/v1/saveProduct", testProduct, ProductDto.class);

        ProductDto createdProduct = response.getBody();
        assertThat(createdProduct.getName()).isNotNull();
        assertThat(createdProduct.getPrice()).isNotNull();
        assertThat(createdProduct.getCategory()).isNotNull();
        assertThat(createdProduct.getStockQuantity()).isNotNull();
        assertThat(createdProduct.getCreatedAt()).isNotNull();
        assertThat(createdProduct.getUpdatedAt()).isNotNull();

        ResponseEntity<ProductDto> getProduct = restTemplate.getForEntity(
                "api/v1/{id}", ProductDto.class, createdProduct.getId());
        assertThat(getProduct.getBody()).isEqualTo(createdProduct);
    }
}