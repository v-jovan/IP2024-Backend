package org.unibl.etf.ip2024.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.unibl.etf.ip2024.models.dto.requests.ActivityRequest;
import org.unibl.etf.ip2024.models.dto.response.ActivityResponse;
import org.unibl.etf.ip2024.services.ActivityService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/activities")
@RequiredArgsConstructor
public class ActivityController {
    private final ActivityService activityService;

    @GetMapping
    public ResponseEntity<List<ActivityResponse>> getAllActivitiesByUser(Principal principal) {
        return ResponseEntity.ok(activityService.getAllActivitiesByUser(principal));
    }

    @PostMapping
    public ResponseEntity<ActivityResponse> addActivity(Principal principal, @RequestBody ActivityRequest activityRequest) {
        return ResponseEntity.ok(activityService.addActivity(principal, activityRequest));
    }
}
