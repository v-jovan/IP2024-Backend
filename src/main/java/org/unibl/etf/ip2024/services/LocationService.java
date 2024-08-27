package org.unibl.etf.ip2024.services;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.unibl.etf.ip2024.models.entities.LocationEntity;

import java.util.List;

@Service
public interface LocationService {
    @Transactional
    LocationEntity addLocation(String name);
    List<LocationEntity> listLocations();
}
