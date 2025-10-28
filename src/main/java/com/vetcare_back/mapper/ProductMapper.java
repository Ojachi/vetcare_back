package com.vetcare_back.mapper;

import com.vetcare_back.dto.product.ProductDTO;
import com.vetcare_back.dto.product.ProductResponseDTO;
import com.vetcare_back.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    Product toEntity(ProductDTO dto);

    ProductResponseDTO toResponseDTO(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    void updateEntity(ProductDTO dto, @MappingTarget Product product);
}