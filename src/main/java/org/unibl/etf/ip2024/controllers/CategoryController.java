package org.unibl.etf.ip2024.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.unibl.etf.ip2024.models.entities.CategoryEntity;
import org.unibl.etf.ip2024.services.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryEntity> getAllCategories() {
        return this.categoryService.listCategories();
    }
}
