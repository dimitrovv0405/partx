package com.example.partx.models.dtos.product;

import com.example.partx.models.dtos.category.CategoryDto;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class ProductDto {
    private UUID id;
    private String name;
    private BigDecimal price;
    private Integer stockQuantity;
    private CategoryDto category;
    private String imageUrl;
    private String description;
}
