package org.unibl.etf.ip2024.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.unibl.etf.ip2024.models.entities.ProgramImageEntity;

@Repository
public interface ProgramImageEntityRepository extends JpaRepository<ProgramImageEntity, Integer> {
}
