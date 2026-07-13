package com.example.partx.controllers.order;

import com.example.partx.models.dtos.order.OrderDto;
import com.example.partx.models.dtos.user.UserDto;
import com.example.partx.services.order.OrderService;
import com.example.partx.services.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {
    private final UserService userService;
    private final OrderService orderService;

    @GetMapping("/orders")
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
}
