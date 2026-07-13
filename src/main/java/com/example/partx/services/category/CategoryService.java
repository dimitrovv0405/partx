package com.example.partx.services.category;

import com.example.partx.models.dtos.category.CategoryDto;
import com.example.partx.repositories.category.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(category -> CategoryDto.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .build()
                )
                .collect(Collectors.toList());
    }
}
