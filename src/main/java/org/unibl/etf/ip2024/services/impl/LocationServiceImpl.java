package org.unibl.etf.ip2024.services.impl;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.unibl.etf.ip2024.exceptions.LocationAlreadyExistsException;
import org.unibl.etf.ip2024.models.entities.LocationEntity;
import org.unibl.etf.ip2024.repositories.LocationEntityRepository;
import org.unibl.etf.ip2024.services.LocationService;

import java.util.List;
import java.util.Optional;

@Service
public class LocationServiceImpl implements LocationService {

    private final LocationEntityRepository locationRepository;

    public LocationServiceImpl(LocationEntityRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

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

        return locationEntity;
    }

    @Override
    public List<LocationEntity> listLocations() {
        return locationRepository.findAll();
    }
}
