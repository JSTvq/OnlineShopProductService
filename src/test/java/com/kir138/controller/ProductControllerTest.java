package com.kir138.controller;

import com.kir138.BaseIntegrationTest;
import com.kir138.model.dto.ProductDto;
import com.kir138.reposity.OutboxProductRepository;
import com.kir138.reposity.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebClient
class ProductControllerTest extends BaseIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OutboxProductRepository outboxProductRepository;

    private ProductDto testProduct;

    private ProductDto testProduct2;

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

        testProduct2 = ProductDto.builder()
                .price(BigDecimal.valueOf(100,33))
                .category("категория2")
                .stockQuantity(56)
                .name("товар2")
                .build();
    }

    @AfterEach
    void tearDown() {
        outboxProductRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    void testGetProductById() {
        ResponseEntity<ProductDto> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/v1/saveProduct", testProduct, ProductDto.class);
        ProductDto createdProduct = response.getBody();

        ResponseEntity<ProductDto> getProduct = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/v1/" + createdProduct.getId(), ProductDto.class);

        assertThat(getProduct.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(createdProduct).isEqualTo(getProduct.getBody());
    }

    @Test
    void testGetProductsByCategory() {
        restTemplate.postForEntity(
                "http://localhost:" + port + "/api/v1/saveProduct", testProduct, ProductDto.class);

        restTemplate.postForEntity(
                "http://localhost:" + port + "/api/v1/saveProduct", testProduct2, ProductDto.class);

        ResponseEntity<List> getProducts = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/v1/getCategory?category=категория2", List.class);

        assertThat(getProducts.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(getProducts.getBody()).hasSize(1);
    }

    @Test
    void testGetAllProducts() {
        restTemplate.postForEntity(
                "http://localhost:" + port + "/api/v1/saveProduct", testProduct, ProductDto.class);

        restTemplate.postForEntity(
                "http://localhost:" + port + "/api/v1/saveProduct", testProduct2, ProductDto.class);

        ResponseEntity<List> getAllProducts = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/v1/allProducts", List.class);

        assertThat(getAllProducts.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(getAllProducts.getBody()).hasSize(2);
    }

    @Test
    void testDeleteProductById() {
        ResponseEntity<ProductDto> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/v1/saveProduct", testProduct, ProductDto.class);
        ProductDto createdProduct = response.getBody();

        restTemplate.delete("http://localhost:" + port + "/api/v1/" + createdProduct.getId());

        ResponseEntity<ProductDto> getProductResponse = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/v1/" + createdProduct.getId(), ProductDto.class);
        assertThat(getProductResponse.getStatusCode().value()).isEqualTo(500);
    }

    @Test
    void testSaveOrUpdateProduct_Update() {
        ResponseEntity<ProductDto> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/v1/saveProduct", testProduct, ProductDto.class);
        ProductDto createdProduct = response.getBody();

        ProductDto updatedProductDto = ProductDto.builder()
                .id(createdProduct.getId())
                .name("upName")
                .stockQuantity(11)
                .category("новая категория")
                .price(BigDecimal.valueOf(320))
                .build();

        ResponseEntity<ProductDto> response2 = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/v1/saveProduct", updatedProductDto, ProductDto.class);
        ProductDto updatedProduct = response2.getBody();

        assertThat(updatedProduct.getId()).isEqualTo(updatedProductDto.getId());
        assertThat(updatedProduct.getName()).isEqualTo(updatedProductDto.getName());
        assertThat(updatedProduct.getStockQuantity()).isEqualTo(updatedProductDto.getStockQuantity());
        assertThat(updatedProduct.getCategory()).isEqualTo(updatedProductDto.getCategory());
        assertThat(updatedProduct.getPrice()).isEqualTo(updatedProductDto.getPrice());
    }

    @Test
    void testSaveOrUpdateProduct_Create() {
        ResponseEntity<ProductDto> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/v1/saveProduct", testProduct, ProductDto.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();

        ProductDto createdProduct = response.getBody();
        assertThat(createdProduct.getName()).isEqualTo(testProduct.getName());
        assertThat(createdProduct.getPrice()).isEqualTo(testProduct.getPrice());
        assertThat(createdProduct.getCategory()).isEqualTo(testProduct.getCategory());
        assertThat(createdProduct.getStockQuantity()).isEqualTo(testProduct.getStockQuantity());
        //assertThat(createdProduct.getCreatedAt()).isNotNull();
        //assertThat(createdProduct.getUpdatedAt()).isNotNull();
    }
}