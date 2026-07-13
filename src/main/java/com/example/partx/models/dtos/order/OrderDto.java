package com.example.partx.models.dtos.order;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class OrderDto {
    private UUID id;
    private String status;
    private LocalDateTime createdAt;
    private BigDecimal totalPrice;
}
