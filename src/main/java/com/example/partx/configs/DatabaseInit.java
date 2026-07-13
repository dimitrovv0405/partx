package com.example.partx.configs;

import com.example.partx.models.entities.category.CategoryEntity;
import com.example.partx.repositories.category.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DatabaseInit implements CommandLineRunner {
    private final CategoryRepository categoryRepository;

    @Override
    public void run(String... args) throws Exception {
        if (categoryRepository.count() == 0) {
            CategoryEntity brakes = new CategoryEntity();
            brakes.setName("Brakes & Suspension");

            CategoryEntity engine = new CategoryEntity();
            engine.setName("Engine Performance");

            CategoryEntity lighting = new CategoryEntity();
            lighting.setName("Lighting & Electrical");

            CategoryEntity body = new CategoryEntity();
            body.setName("Body Panels & Exterior");

            categoryRepository.saveAll(List.of(brakes, engine, lighting, body));
        }
    }
}
