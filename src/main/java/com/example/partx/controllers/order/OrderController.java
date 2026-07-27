package com.example.partx.controllers.order;

import com.example.partx.models.dtos.CartFormDto;
import com.example.partx.models.dtos.order.OrderDto;
import com.example.partx.models.dtos.user.UserDto;
import com.example.partx.models.entities.order.OrderEntity;
import com.example.partx.models.entities.user.UserEntity;
import com.example.partx.models.enums.order.OrderStatus;
import com.example.partx.repositories.category.CategoryRepository;
import com.example.partx.repositories.order.OrderRepository;
import com.example.partx.repositories.product.ProductRepository;
import com.example.partx.repositories.user.UserRepository;
import com.example.partx.services.category.CategoryService;
import com.example.partx.services.order.OrderService;
import com.example.partx.services.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {
    private final UserService userService;
    private final OrderService orderService;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    @GetMapping
    public ModelAndView getOrdersPage(@AuthenticationPrincipal UserDetails userDetails) {
        ModelAndView modelAndView = new ModelAndView();

        UserDto userDto = userService.getByUsername(userDetails.getUsername());
        modelAndView.addObject("username", userDto.getUsername());
        modelAndView.addObject("userRole", userDto.getRole());
        modelAndView.addObject("balance", userDto.getUserBalance());

        List<OrderDto> usersOrders = orderService.getOrdersByUserId(userDto.getId());
        modelAndView.addObject("orders", usersOrders);

        modelAndView.setViewName("orders");

        return modelAndView;
    }

    @PostMapping("/{id}/cycle-status")
    public ModelAndView cycleOrderStatus(@PathVariable UUID id,
                                         @AuthenticationPrincipal UserDetails currentUser) {
        if (currentUser == null) {
            return new ModelAndView("redirect:/auth/login");
        }

        Optional<OrderEntity> optionalOrder = orderRepository.findById(id);
        if (optionalOrder.isPresent()) {
            OrderEntity order = optionalOrder.get();
            OrderStatus currentStatus = order.getOrderStatus();

            OrderStatus[] allStatuses = OrderStatus.values();
            int nextIdx = (currentStatus.ordinal() + 1) % allStatuses.length;
            order.setOrderStatus(allStatuses[nextIdx]);

            orderRepository.save(order);
        }

        UserEntity freelancer = userService.findUserEntityByUsername(currentUser.getUsername());

        ModelAndView modelAndView = new ModelAndView("freelancer");
        modelAndView.addObject("products",
                productRepository.findAllByOwnerId(freelancer.getId()));
        modelAndView.addObject("orders",
                orderRepository.findAllOrdersByFreelancerProducts(freelancer.getId()));
        modelAndView.addObject("categories", categoryService.getAllCategories());
        modelAndView.addObject("username", freelancer.getUsername());
        modelAndView.addObject("balance", freelancer.getUserBalance());

        return modelAndView;
    }

    @PostMapping("/checkout")
    public ModelAndView handleCheckout(@ModelAttribute("cartForm") CartFormDto cartForm, Principal principal) {
        if (principal == null) {
            return new ModelAndView("redirect:/auth/login");
        }

        try {
            UserEntity buyer = userRepository.findByUsername(principal.getName())
                    .orElseThrow(() -> new IllegalArgumentException("Buyer account not found"));

            OrderEntity finishedOrder = orderService.checkout(buyer, cartForm.getItems());

            return new ModelAndView("redirect:/orders/success/" + finishedOrder.getId());

        } catch (Exception e) {
            ModelAndView errorView = new ModelAndView("cart");
            errorView.addObject("errorMessage", e.getMessage());
            errorView.addObject("username", principal.getName());

            userRepository.findByUsername(principal.getName())
                    .ifPresent(user -> errorView.addObject("balance", user.getUserBalance()));

            return errorView;
        }
    }

    @GetMapping("/success/{orderId}")
    public ModelAndView showSuccessPage(@PathVariable String orderId, Principal principal) {
        ModelAndView modelAndView = new ModelAndView("order-success");

        if (principal != null) {
            modelAndView.addObject("username", principal.getName());

            userRepository.findByUsername(principal.getName())
                    .ifPresent(user -> modelAndView.addObject("balance", user.getUserBalance()));
        }

        modelAndView.addObject("orderId", orderId);

        return modelAndView;
    }

    @GetMapping("/modal/{id}")
    public ModelAndView getOrderModalDetails(@PathVariable UUID id) {
        OrderDto order = orderService.getOrderById(id);

        ModelAndView mav = new ModelAndView("order-modal-content :: orderModalContent");

        if (order == null) {
            mav.addObject("error", "Order not found.");
            return mav;
        }

        mav.addObject("order", order);
        return mav;
    }
}