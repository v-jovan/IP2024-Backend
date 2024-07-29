package org.unibl.etf.ip2024.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.unibl.etf.ip2024.services.EmailService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestController {
    private final EmailService emailService;

    @GetMapping("/email")
    public ResponseEntity<String> sendTestEmail(@RequestParam String to) {
        String activationLink = "http://localhost:4200/activate?token=testtoken";
        emailService.sendActivationEmail(to, activationLink);
        return ResponseEntity.ok("Email sent to " + to);
    }
}
