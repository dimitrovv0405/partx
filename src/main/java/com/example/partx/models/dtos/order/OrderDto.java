package com.example.partx.models.dtos.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import com.example.partx.models.dtos.orderItem.OrderItemDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private UUID id;
    private String status;
    private LocalDate createdAt;
    private BigDecimal totalPrice;
    private List<OrderItemDto> items;
}