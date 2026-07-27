package com.example.partx.controllers;


import com.example.partx.models.dtos.product.ProductAddDto;
import com.example.partx.models.entities.category.CategoryEntity;
import com.example.partx.models.entities.order.OrderEntity;
import com.example.partx.models.entities.product.ProductEntity;
import com.example.partx.models.entities.user.UserEntity;
import com.example.partx.repositories.category.CategoryRepository;
import com.example.partx.repositories.order.OrderRepository;
import com.example.partx.repositories.product.ProductRepository;
import com.example.partx.services.category.CategoryService;
import com.example.partx.services.product.ProductService;
import com.example.partx.services.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.UUID;


@Controller
@RequiredArgsConstructor
public class IndexController {
    private final UserService userService;
    private final CategoryService categoryService;
    private final ProductService productService;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("totalProducts", productRepository.count());
        return "index";
    }

    @GetMapping("/home")
    public String showHomePage(Model model, Principal principal) {
        if (principal != null) {
            String currentUsername = principal.getName();

            UserEntity user = userService.findUserEntityByUsername(currentUsername);

            model.addAttribute("balance", user.getUserBalance() != null ? user.getUserBalance() : BigDecimal.ZERO);
            model.addAttribute("username", user.getUsername());
            model.addAttribute("userRole", user.getRole().toString());
            model.addAttribute("totalProducts", productRepository.count());
        } else {
            model.addAttribute("balance", BigDecimal.ZERO);
        }

        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("featuredProducts", productService.getAllProducts());

        return "home";
    }

    @GetMapping("/freelancer")
    public ModelAndView showFreelancerDashboard(@AuthenticationPrincipal UserDetails currentUser) {
        ModelAndView modelAndView = new ModelAndView("freelancer");

        if (currentUser == null) {
            return new ModelAndView("redirect:/users/login");
        }

        UserEntity freelancer = userService.findUserEntityByUsername(currentUser.getUsername());
        List<ProductEntity> freelancerProducts = productRepository.findAllByOwnerId(freelancer.getId());
        List<OrderEntity> clientOrders = orderRepository.findAllOrdersByFreelancerProducts(freelancer.getId());

        modelAndView.addObject("products", freelancerProducts);
        modelAndView.addObject("orders", clientOrders);
        modelAndView.addObject("categories", categoryService.getAllCategories());

        modelAndView.addObject("username", freelancer.getUsername());
        modelAndView.addObject("balance", freelancer.getUserBalance());

        return modelAndView;
    }

    @PostMapping("/freelancer/add-product")
    public ModelAndView handleCreateProduct(@ModelAttribute ProductAddDto dto,
                                            @AuthenticationPrincipal UserDetails currentUser) {
        if (currentUser == null) {
            return new ModelAndView("redirect:/auth/login");
        }

        ProductEntity product;

        // Fetch existing product if ID is provided, otherwise create a new one
        if (dto.getId() != null) {
            product = productRepository.findById(dto.getId())
                    .orElseGet(ProductEntity::new);
        } else {
            product = new ProductEntity();
            UserEntity owner = userService.findUserEntityByUsername(currentUser.getUsername());
            product.setOwner(owner);
        }

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStockAmount(dto.getStock());
        product.setImageUrl(dto.getImageUrl());

        if (dto.getCategoryId() != null) {
            CategoryEntity category = categoryRepository.findById(dto.getCategoryId())
                    .orElse(null);
            product.setCategory(category);
        }

        productRepository.save(product);

        return new ModelAndView("redirect:/freelancer");
    }

    @PostMapping("/freelancer/delete-product")
    public ModelAndView handleDeleteProduct(@RequestParam UUID id,
                                            @AuthenticationPrincipal UserDetails currentUser) {
        if (currentUser == null) {
            return new ModelAndView("redirect:/auth/login");
        }

        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
        }

        return new ModelAndView("redirect:/freelancer");
    }
}
