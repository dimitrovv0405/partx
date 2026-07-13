package com.example.partx.models.dtos.product;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class ProductAddDto {
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private UUID categoryId;
    private String imageUrl;
}
