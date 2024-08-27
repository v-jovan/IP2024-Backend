package org.unibl.etf.ip2024.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.unibl.etf.ip2024.models.entities.AttributeEntity;

import java.util.List;

@Repository
public interface AttributeEntityRepository extends JpaRepository<AttributeEntity, Integer> {
    List<AttributeEntity> findByCategoryId(Integer categoryId);
}
