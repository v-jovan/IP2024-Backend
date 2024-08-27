package org.unibl.etf.ip2024.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.unibl.etf.ip2024.models.entities.LocationEntity;

import java.util.Optional;

@Repository
public interface LocationEntityRepository extends JpaRepository<LocationEntity, Integer> {
    Optional<LocationEntity> findByName(String name);
}
