package org.unibl.etf.ip2024.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.unibl.etf.ip2024.models.entities.AttributeEntity;

public interface AttributeEntityRepository extends JpaRepository<AttributeEntity, Integer> {
}
