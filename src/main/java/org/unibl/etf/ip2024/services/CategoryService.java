package org.unibl.etf.ip2024.services;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.unibl.etf.ip2024.models.dto.requests.CategoryRequest;
import org.unibl.etf.ip2024.models.entities.CategoryEntity;

import java.util.List;

@Service
public interface CategoryService {
    @Transactional
    CategoryEntity addCategory(CategoryRequest categoryRequest);
    List<CategoryEntity> listCategories();
}
