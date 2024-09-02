package org.unibl.etf.ip2024.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.unibl.etf.ip2024.models.entities.AttributeValueEntity;

import java.util.List;

@Repository
public interface AttributeValueEntityRepository extends JpaRepository<AttributeValueEntity, Integer> {
    List<AttributeValueEntity> findByAttributeId(Integer attributeId);
}
