package org.unibl.etf.ip2024.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.unibl.etf.ip2024.models.entities.LocationEntity;

public interface LocationEntityRepository extends JpaRepository<LocationEntity, Integer> {
}
