package com.kir138.mapper;

import com.kir138.model.dto.ProductDto;
import com.kir138.model.entity.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductDto toMapper(Product product);

    Product toEntity(ProductDto productDto);
}