package org.unibl.etf.ip2024.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.unibl.etf.ip2024.models.entities.LocationEntity;
import org.unibl.etf.ip2024.services.LocationService;

import java.util.List;

@RestController
@RequestMapping("/location")
@RequiredArgsConstructor
public class LocationController {
    private final LocationService locationService;

    // Endpoint to get all locations
    @GetMapping
    public List<LocationEntity> getAllLocations() {
        return this.locationService.listLocations();
    }

}
