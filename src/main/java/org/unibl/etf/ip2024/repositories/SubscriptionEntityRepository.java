package org.unibl.etf.ip2024.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.unibl.etf.ip2024.models.entities.CategoryEntity;
import org.unibl.etf.ip2024.models.entities.SubscriptionEntity;
import org.unibl.etf.ip2024.models.entities.UserEntity;

import java.util.List;

@Repository
public interface SubscriptionEntityRepository extends JpaRepository<SubscriptionEntity, Integer> {
    List<SubscriptionEntity> findAllByUser(UserEntity user);
    List<SubscriptionEntity> findAllByCategory(CategoryEntity category);
    Boolean existsByUserAndCategory(UserEntity user, CategoryEntity category);
    void deleteByUserAndCategory(UserEntity user, CategoryEntity category);
}
