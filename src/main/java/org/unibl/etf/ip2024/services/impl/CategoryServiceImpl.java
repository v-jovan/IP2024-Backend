package org.unibl.etf.ip2024.services.impl;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.unibl.etf.ip2024.exceptions.CategoryAlreadyExistsException;
import org.unibl.etf.ip2024.exceptions.CategoryNotFoundException;
import org.unibl.etf.ip2024.exceptions.UserNotFoundException;
import org.unibl.etf.ip2024.models.dto.CategoryWithSubscriptionDTO;
import org.unibl.etf.ip2024.models.dto.requests.CategoryRequest;
import org.unibl.etf.ip2024.models.entities.CategoryEntity;
import org.unibl.etf.ip2024.models.entities.SubscriptionEntity;
import org.unibl.etf.ip2024.models.entities.UserEntity;
import org.unibl.etf.ip2024.repositories.CategoryEntityRepository;
import org.unibl.etf.ip2024.repositories.SubscriptionEntityRepository;
import org.unibl.etf.ip2024.repositories.UserEntityRepository;
import org.unibl.etf.ip2024.services.CategoryService;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryEntityRepository categoryRepository;
    private final UserEntityRepository userRepository;
    private final SubscriptionEntityRepository subscriptionRepository;

    public CategoryServiceImpl(CategoryEntityRepository categoryRepository, UserEntityRepository userRepository, SubscriptionEntityRepository subscriptionRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.subscriptionRepository = subscriptionRepository;
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

    @Override
    public List<CategoryWithSubscriptionDTO> getCategoriesWithSubscription(Principal principal) {
        UserEntity user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("Korisnik nije pronađen"));

        List<SubscriptionEntity> subscriptions = this.subscriptionRepository.findAllByUser(user);
        List<CategoryEntity> categories = this.categoryRepository.findAll();

        Set<Integer> subscribedCategoryIds = subscriptions
                .stream()
                .map(subscription -> subscription.getCategory().getId())
                .collect(Collectors.toSet());


        return categories.stream()
                .map(category -> {
                    CategoryWithSubscriptionDTO dto = new CategoryWithSubscriptionDTO();
                    dto.setId(category.getId());
                    dto.setName(category.getName());
                    dto.setDescription(category.getDescription());
                    dto.setSubscribed(subscribedCategoryIds.contains(category.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CategoryEntity addSubscription(Principal principal, Integer categoryId) {
        UserEntity user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("Korisnik nije pronađen"));

        CategoryEntity category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Kategorija sa id-em '" + categoryId + "' ne postoji."));

        Boolean isSubscribed = subscriptionRepository.existsByUserAndCategory(user, category);

        if (isSubscribed) {
            subscriptionRepository.deleteByUserAndCategory(user, category);
        } else {
            SubscriptionEntity subscription = new SubscriptionEntity();
            subscription.setUser(user);
            subscription.setCategory(category);
            subscriptionRepository.saveAndFlush(subscription);
        }

        return category;
    }
}
