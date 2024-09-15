package org.unibl.etf.ip2024.services.impl;

import org.springframework.stereotype.Service;
import org.unibl.etf.ip2024.models.entities.CityEntity;
import org.unibl.etf.ip2024.repositories.CityEntityRepository;
import org.unibl.etf.ip2024.services.CityService;
import org.unibl.etf.ip2024.services.LogService;

import java.util.List;

@Service
public class CityServiceImpl implements CityService {

    private final CityEntityRepository repository;
    private final LogService logService;

    public CityServiceImpl(CityEntityRepository repository, LogService logService) {
        this.repository = repository;
        this.logService = logService;
    }

    @Override
    public CityEntity addCity(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Grad ne može biti prazan.");
        }

        logService.log(null, "Dodavanje grada " + name);

        return this.repository.saveAndFlush(new CityEntity(name));
    }

    @Override
    public CityEntity getCityById(int id) {
        logService.log(null, "Prikaz grada sa id " + id);
        return this.repository.findById(id).orElse(null);
    }

    @Override
    public List<CityEntity> getCities() {
        logService.log(null, "Prikaz svih gradova");
        return this.repository.findAll();
    }
}

