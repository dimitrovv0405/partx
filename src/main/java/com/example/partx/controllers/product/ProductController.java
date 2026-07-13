package com.example.partx.controllers.product;

import com.example.partx.models.dtos.category.CategoryDto;
import com.example.partx.models.dtos.product.ProductDto;
import com.example.partx.models.dtos.user.UserDto;
import com.example.partx.models.entities.product.ProductEntity;
import com.example.partx.repositories.product.ProductRepository;
import com.example.partx.services.category.CategoryService;
import com.example.partx.services.product.ProductService;
import com.example.partx.services.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/shop")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final CategoryService categoryService;
    private final UserService userService;
    private final ProductRepository productRepository;

    @GetMapping
    public ModelAndView getHomeShop(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(name = "categoryId", required = false) String categoryId) {
        ModelAndView modelAndView = new ModelAndView();

        UserDto userDto = userService.getByUsername(userDetails.getUsername());
        modelAndView.addObject("username", userDto.getUsername());
        modelAndView.addObject("userRole", userDto.getRole());
        modelAndView.addObject("balance", userDto.getUserBalance());

        List<CategoryDto> categories = categoryService.getAllCategories();
        modelAndView.addObject("categories", categories);

        List<ProductDto> products;
        if (categoryId != null && !categoryId.trim().isEmpty()) {
            products = productService.getProductsByCategoryId(categoryId);
        } else {
            products = productService.getAllProducts();
        }
        modelAndView.addObject("products", products);

        List<ProductEntity> globalInventory = productRepository.findAll();
        modelAndView.addObject("featuredProducts", globalInventory);

        modelAndView.setViewName("shop");

        return modelAndView;
    }
}