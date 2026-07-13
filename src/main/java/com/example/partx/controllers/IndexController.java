package com.example.partx.controllers;

import com.example.partx.models.dtos.category.CategoryDto;
import com.example.partx.models.dtos.product.ProductAddDto;
import com.example.partx.models.entities.category.CategoryEntity;
import com.example.partx.models.entities.product.ProductEntity;
import com.example.partx.models.entities.user.UserEntity;
import com.example.partx.repositories.category.CategoryRepository;
import com.example.partx.repositories.product.ProductRepository;
import com.example.partx.services.category.CategoryService;
import com.example.partx.services.product.ProductService;
import com.example.partx.services.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class IndexController {
    private final UserService userService;
    private final CategoryService categoryService;
    private final ProductService productService;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

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
    public String showFreelancerDashboard(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/auth/login";
        }

        String currentUsername = principal.getName();
        UserEntity user = userService.findUserEntityByUsername(currentUsername);

        if (!user.getRole().toString().equals("FREELANCER")) {
            return "redirect:/home?error=unauthorized";
        }

        List<CategoryDto> categoryDtos = categoryService.getAllCategories();
        model.addAttribute("categories", categoryDtos);

        model.addAttribute("username", user.getUsername());
        model.addAttribute("balance",
                user.getUserBalance() != null ? user.getUserBalance() : BigDecimal.ZERO);
        model.addAttribute("userRole", user.getRole().toString());

        return "freelancer";
    }

    @PostMapping("/freelancer/add-product")
    public ModelAndView handleCreateProduct(@ModelAttribute ProductAddDto dto) {
        ProductEntity product = new ProductEntity();
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
}
