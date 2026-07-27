package com.example.partx.services.order;

import com.example.partx.mappers.order.OrderMapper;
import com.example.partx.models.dtos.order.CartItemDto;
import com.example.partx.models.dtos.order.OrderDto;
import com.example.partx.models.entities.order.OrderEntity;
import com.example.partx.models.entities.orderItem.OrderItemEntity;
import com.example.partx.models.entities.product.ProductEntity;
import com.example.partx.models.entities.user.UserEntity;
import com.example.partx.models.enums.order.OrderStatus;
import com.example.partx.repositories.order.OrderRepository;
import com.example.partx.repositories.product.ProductRepository;
import com.example.partx.repositories.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    public List<OrderDto> getOrdersByUserId(UUID userId) {
        return orderRepository.findAllByUserId(userId)
                .stream()
                .map(order -> OrderDto.builder()
                        .id(order.getId())
                        .status(order.getOrderStatus().name())
                        .createdAt(LocalDate.from(order.getOrderDate()))
                        .totalPrice(order.getTotalPrice())
                        .build())
                .collect(Collectors.toList());
    }

    public OrderEntity checkout(UserEntity buyer, List<CartItemDto> cartItems) {
        BigDecimal totalCost = BigDecimal.ZERO;
        List<OrderItemEntity> orderItems = new ArrayList<>();

        OrderEntity order = OrderEntity.builder()
                .user(buyer)
                .orderStatus(OrderStatus.PENDING)
                .orderDate(LocalDate.now())
                .build();

        for (CartItemDto item : cartItems) {
            ProductEntity product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));

            if (product.getStockAmount() < item.getQuantity()) {
                throw new IllegalStateException("Insufficient stock for: " + product.getName());
            }

            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

            totalCost = totalCost.add(itemTotal);

            product.setStockAmount(product.getStockAmount() - item.getQuantity());
            productRepository.save(product);

            UserEntity freelancer = product.getOwner();
            if (freelancer != null) {
                freelancer.setUserBalance(freelancer.getUserBalance().add(itemTotal));
                userRepository.save(freelancer);
            }

            OrderItemEntity orderItem = OrderItemEntity.builder()
                    .order(order)
                    .product(product)
                    .quantity(item.getQuantity())
                    .priceAtPurchase(product.getPrice())
                    .build();
            orderItems.add(orderItem);
        }

        if (buyer.getUserBalance().compareTo(totalCost) < 0) {
            throw new IllegalStateException("Insufficient balance to complete purchase.");
        }

        buyer.setUserBalance(buyer.getUserBalance().subtract(totalCost));
        userRepository.save(buyer);

        order.setTotalPrice(totalCost);
        order.setItems(orderItems);

        return orderRepository.save(order);
    }

    public OrderDto getOrderById(UUID id) {
        OrderEntity order = orderRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Order not found.")
        );

        if (order == null) {
            return null;
        }

        return orderMapper.toDto(order);
    }
}
