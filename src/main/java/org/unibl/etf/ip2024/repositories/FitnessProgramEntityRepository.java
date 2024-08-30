package org.unibl.etf.ip2024.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.unibl.etf.ip2024.models.entities.FitnessProgramEntity;

import java.util.Optional;

@Repository
public interface FitnessProgramEntityRepository extends JpaRepository<FitnessProgramEntity, Integer> {
    Optional<FitnessProgramEntity> findByName(String name);
    Page<FitnessProgramEntity> findAllByUserId(Integer userId, Pageable pageable);
}
