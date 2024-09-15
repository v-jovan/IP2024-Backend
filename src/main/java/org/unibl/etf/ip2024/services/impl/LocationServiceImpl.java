package org.unibl.etf.ip2024.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.unibl.etf.ip2024.exceptions.LocationAlreadyExistsException;
import org.unibl.etf.ip2024.models.entities.LocationEntity;
import org.unibl.etf.ip2024.repositories.LocationEntityRepository;
import org.unibl.etf.ip2024.services.LocationService;
import org.unibl.etf.ip2024.services.LogService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationEntityRepository locationRepository;
    private final LogService logService;

    @Override
    @Transactional
    public LocationEntity addLocation(String name) {
        Optional<LocationEntity> existingLocation = locationRepository.findByName(name);
        if (existingLocation.isPresent()) {
            throw new LocationAlreadyExistsException("Lokacija sa imenom '" + name + "' već postoji.");
        }
        LocationEntity locationEntity = new LocationEntity();
        locationEntity.setName(name);
        locationRepository.saveAndFlush(locationEntity);

        logService.log(null, "Dodavanje lokacije");

        return locationEntity;
    }

    @Override
    public List<LocationEntity> listLocations() {
        logService.log(null, "Prikaz svih lokacija");
        return locationRepository.findAll();
    }
}
