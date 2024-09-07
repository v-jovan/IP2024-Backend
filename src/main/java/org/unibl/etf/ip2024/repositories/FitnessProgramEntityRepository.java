package org.unibl.etf.ip2024.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.unibl.etf.ip2024.models.entities.CategoryEntity;
import org.unibl.etf.ip2024.models.entities.FitnessProgramEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FitnessProgramEntityRepository extends JpaRepository<FitnessProgramEntity, Integer> {
    Optional<FitnessProgramEntity> findByName(String name);
    Page<FitnessProgramEntity> findAllByUserId(Integer userId, Pageable pageable);
    Page<FitnessProgramEntity> findDistinctByProgramAttributes_AttributeValue_Id(Integer attributeValueId, Pageable pageable);
    Page<FitnessProgramEntity> findDistinctByProgramAttributes_AttributeValue_IdIn(List<Integer> attributeValueIds, Pageable pageable);
    Page<FitnessProgramEntity> findAllByCategoryId(Integer categoryId, Pageable pageable);
    List<FitnessProgramEntity> findAllByCategoryAndCreatedAtAfter(CategoryEntity category, LocalDateTime createdAt);
}
