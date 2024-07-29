package org.unibl.etf.ip2024.services;

import org.springframework.stereotype.Service;
import org.unibl.etf.ip2024.models.entities.CityEntity;

import java.util.List;

@Service
public interface CityService {
    CityEntity addCity(String name);
    CityEntity getCityById(int id);
    List<CityEntity> getCities();
}
