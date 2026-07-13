package com.example.partx.services.order;

import com.example.partx.models.dtos.order.OrderDto;
import com.example.partx.repositories.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    public List<OrderDto> getOrdersByUserId(UUID userId) {
        return orderRepository.findAllByUserId(userId)
                .stream()
                .map(order -> OrderDto.builder()
                        .id(order.getId())
                        .status(order.getOrderStatus().name())
                        .createdAt(LocalDateTime.from(order.getOrderDate()))
                        .totalPrice(order.getTotalPrice())
                        .build())
                .collect(Collectors.toList());
    }
}
