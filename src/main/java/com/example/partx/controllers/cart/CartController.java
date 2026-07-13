package com.example.partx.controllers.cart;

import com.example.partx.models.dtos.user.UserDto;
import com.example.partx.services.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
public class CartController {
    private final UserService userService;

    @GetMapping("/cart")
    public ModelAndView getCartPage(@AuthenticationPrincipal UserDetails userDetails) {
        ModelAndView modelAndView = new ModelAndView();

        UserDto userDto = userService.getByUsername(userDetails.getUsername());
        modelAndView.addObject("username", userDto.getUsername());
        modelAndView.addObject("userRole", userDto.getRole());
        modelAndView.addObject("balance", userDto.getUserBalance());

        modelAndView.setViewName("cart");

        return modelAndView;
    }
}
