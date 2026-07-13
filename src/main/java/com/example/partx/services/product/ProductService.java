package com.example.partx.services.product;

import com.example.partx.models.dtos.category.CategoryDto;
import com.example.partx.models.dtos.product.ProductDto;
import com.example.partx.models.entities.category.CategoryEntity;
import com.example.partx.models.entities.product.ProductEntity;
import com.example.partx.repositories.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public List<ProductDto> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(product -> {
                    CategoryDto categoryDto = null;
                    if (product.getCategory() != null) {
                        categoryDto = CategoryDto.builder()
                                .id(product.getCategory().getId())
                                .name(product.getCategory().getName())
                                .build();
                    }

                    return ProductDto.builder()
                            .id(product.getId())
                            .imageUrl(product.getImageUrl())
                            .name(product.getName())
                            .price(product.getPrice())
                            .stockQuantity(product.getStockAmount())
                            .category(categoryDto)
                            .build();
                })
                .collect(Collectors.toList());
    }

    public List<ProductDto> getProductsByCategoryId(String categoryId) {
        // Convert the incoming String parameter into a genuine UUID object
        UUID uuidCategoryId = UUID.fromString(categoryId);

        return productRepository.findByCategoryId(uuidCategoryId)
                .stream()
                .map(product -> {
                    CategoryDto categoryDto = null;
                    if (product.getCategory() != null) {
                        categoryDto = CategoryDto.builder()
                                .id(product.getCategory().getId())
                                .name(product.getCategory().getName())
                                .build();
                    }

                    return ProductDto.builder()
                            .id(product.getId())
                            .name(product.getName())
                            .price(product.getPrice())
                            .imageUrl(product.getImageUrl())
                            .stockQuantity(product.getStockAmount())
                            .category(categoryDto)
                            .build();
                })
                .toList();
    }


    public ProductDto getProductById(UUID id) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + id));

        CategoryDto categoryDto = null;
        if (product.getCategory() != null) {
            categoryDto = CategoryDto.builder()
                    .id(product.getCategory().getId())
                    .name(product.getCategory().getName())
                    .build();
        }

        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .imageUrl(product.getImageUrl())
                .stockQuantity(product.getStockAmount())
                .category(categoryDto)
                .build();
    }
}
