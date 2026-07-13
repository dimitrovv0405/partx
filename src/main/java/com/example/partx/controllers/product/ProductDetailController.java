package com.example.partx.controllers.product;

import java.util.UUID;

import com.example.partx.models.dtos.product.ProductDto;
import com.example.partx.services.product.ProductService;
import com.example.partx.services.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductDetailController {
    private final ProductService productService;
    private final UserService userService;

    @GetMapping("/{id}")
    public String getProductDetail(@PathVariable UUID id, Model model, Principal principal) {
        ProductDto product = productService.getProductById(id);
        model.addAttribute("product", product);

        if (principal != null) {
            String username = principal.getName();
            model.addAttribute("username", username);

            model.addAttribute("balance", userService.getBalanceByUsername(username));
            model.addAttribute("userRole", userService.getRoleByUsername(username));
        } else {
            model.addAttribute("username", null);
            model.addAttribute("balance", "0.00");
            model.addAttribute("userRole", "GUEST");
        }
        return "product-detail";
    }
}