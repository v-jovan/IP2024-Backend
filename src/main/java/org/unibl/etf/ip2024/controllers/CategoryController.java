package org.unibl.etf.ip2024.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.unibl.etf.ip2024.models.dto.CategoryWithSubscription;
import org.unibl.etf.ip2024.models.dto.requests.SubscriptionRequest;
import org.unibl.etf.ip2024.models.entities.CategoryEntity;
import org.unibl.etf.ip2024.services.CategoryService;

import java.security.Principal;
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

    @GetMapping("/subscriptions")
    public ResponseEntity<List<CategoryWithSubscription>> getCategoriesWithSubscription(Principal principal) {
        return ResponseEntity.ok(this.categoryService.getCategoriesWithSubscription(principal));
    }

    @PostMapping("/subscribe")
    public ResponseEntity<CategoryEntity> addCategory(Principal principal, @RequestBody SubscriptionRequest request) {
        return ResponseEntity.ok(this.categoryService.addSubscription(principal, request.getCategoryId()));
    }
}
