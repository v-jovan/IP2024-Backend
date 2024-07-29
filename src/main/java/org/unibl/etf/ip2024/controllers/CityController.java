package org.unibl.etf.ip2024.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.unibl.etf.ip2024.models.dto.requests.CityRequest;
import org.unibl.etf.ip2024.models.entities.CityEntity;
import org.unibl.etf.ip2024.services.CityService;

import java.util.List;

@RestController
@RequestMapping("/cities")
@RequiredArgsConstructor
public class CityController {
    private final CityService cityService;

    // Endpoint to get all cities
    @GetMapping
    public List<CityEntity> getAllCities() {
        return this.cityService.getCities();
    }

    // Endpoint to add a new city
    @PostMapping
    public CityEntity addCity(@RequestBody CityRequest request) {
        if (request.getName() == null || request.getName().isEmpty()) {
            throw new IllegalArgumentException("Nepotpuni podaci o gradu.");
        }
        return this.cityService.addCity(request.getName());
    }
}
