package com.example.partx.mappers.order;

import com.example.partx.models.dtos.order.OrderDto;
import com.example.partx.models.dtos.orderItem.OrderItemDto;
import com.example.partx.models.entities.order.OrderEntity;
import com.example.partx.models.entities.orderItem.OrderItemEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.stream.Collectors;


@Component
public class OrderMapper {
    public OrderDto toDto(OrderEntity order) {
        if (order == null) {
            return null;
        }

        OrderDto dto = new OrderDto();
        dto.setId(order.getId() != null ? order.getId() : null);
        dto.setStatus(order.getOrderStatus() != null ? order.getOrderStatus().name() : "PENDING");
        dto.setTotalPrice(order.getTotalPrice());
        dto.setCreatedAt(order.getOrderDate());

        if (order.getItems() != null) {
            dto.setItems(order.getItems().stream()
                    .map(this::toItemDto)
                    .collect(Collectors.toList()));
        } else {
            dto.setItems(Collections.emptyList());
        }

        return dto;
    }

    private OrderItemDto toItemDto(OrderItemEntity item) {
        if (item == null) {
            return null;
        }

        OrderItemDto itemDto = new OrderItemDto();
        itemDto.setProductName(item.getProduct().getName());
        itemDto.setQuantity(item.getQuantity());
        itemDto.setPrice(item.getPriceAtPurchase());

        return itemDto;
    }
}
