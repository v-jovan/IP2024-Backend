package org.unibl.etf.ip2024.services.impl;

import org.springframework.stereotype.Service;
import org.unibl.etf.ip2024.models.entities.CityEntity;
import org.unibl.etf.ip2024.repositories.CityEntityRepository;
import org.unibl.etf.ip2024.services.CityService;

import java.util.List;

@Service
public class CityServiceImpl implements CityService {

    private final CityEntityRepository repository;

    public CityServiceImpl(CityEntityRepository repository) {
        this.repository = repository;
    }

    @Override
    public CityEntity addCity(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("City name cannot be null or empty");
        }
        return this.repository.saveAndFlush(new CityEntity(name));
    }

    @Override
    public CityEntity getCityById(int id) {
        return this.repository.findById(id).orElse(null);
    }

    @Override
    public List<CityEntity> getCities() {
        return this.repository.findAll();
    }
}

