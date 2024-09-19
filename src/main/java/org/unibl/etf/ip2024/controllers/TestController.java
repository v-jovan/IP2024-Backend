package org.unibl.etf.ip2024.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.unibl.etf.ip2024.models.dto.requests.CategoryRequest;
import org.unibl.etf.ip2024.models.entities.CategoryEntity;
import org.unibl.etf.ip2024.models.entities.LocationEntity;
import org.unibl.etf.ip2024.services.CategoryService;
import org.unibl.etf.ip2024.services.EmailService;
import org.unibl.etf.ip2024.services.LocationService;
import org.unibl.etf.ip2024.services.SubscriptionService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestController {

    @Value("${frontend.url}")
    private String frontendUrl;

    private final EmailService emailService;
    private final CategoryService categoryService;
    private final LocationService locationService;
    private final SubscriptionService subscriptionService;


    @GetMapping("/test-email")
    public String sendTestEmail() {
        subscriptionService.sendDailySubscriptionEmails();
        return "Test email sent!";
    }

    @GetMapping("/email")
    public ResponseEntity<String> sendTestEmail(@RequestParam String to) {
        String token = UUID.randomUUID().toString();
        String activationLink = frontendUrl + "/activate?token=" + token;
        emailService.sendActivationEmail(to, activationLink);
        return ResponseEntity.ok("Email sent to " + to);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryEntity>> listCategories() {
        return ResponseEntity.ok(categoryService.listCategories());
    }

    @PostMapping("/categories")
    public ResponseEntity<CategoryEntity> addCategory(@RequestBody CategoryRequest categoryRequest) {
        return ResponseEntity.ok(categoryService.addCategory(categoryRequest));
    }

    @PostMapping("/locations")
    public ResponseEntity<LocationEntity> addLocation(@RequestBody String name) {
        return ResponseEntity.ok(locationService.addLocation(name));
    }

    @GetMapping("/locations")
    public ResponseEntity<List<LocationEntity>> listLocations() {
        return ResponseEntity.ok(locationService.listLocations());
    }
}
