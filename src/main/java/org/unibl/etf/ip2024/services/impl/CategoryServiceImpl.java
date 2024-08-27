package org.unibl.etf.ip2024.services.impl;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.unibl.etf.ip2024.exceptions.CategoryAlreadyExistsException;
import org.unibl.etf.ip2024.models.dto.requests.CategoryRequest;
import org.unibl.etf.ip2024.models.entities.CategoryEntity;
import org.unibl.etf.ip2024.repositories.CategoryEntityRepository;
import org.unibl.etf.ip2024.services.CategoryService;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryEntityRepository categoryRepository;

    public CategoryServiceImpl(CategoryEntityRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }


    @Override
    @Transactional
    public CategoryEntity addCategory(CategoryRequest categoryRequest) {

        Optional<CategoryEntity> existingCategory = categoryRepository.findByName(categoryRequest.getName());
        if (existingCategory.isPresent()) {
            throw new CategoryAlreadyExistsException("Kategorija sa imenom '" + categoryRequest.getName() + "' već postoji.");
        }

        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setName(categoryRequest.getName());
        categoryEntity.setDescription(categoryRequest.getDescription());
        categoryRepository.saveAndFlush(categoryEntity);

        return categoryEntity;
    }

    @Override
    public List<CategoryEntity> listCategories() {
        return categoryRepository.findAll();
    }
}
