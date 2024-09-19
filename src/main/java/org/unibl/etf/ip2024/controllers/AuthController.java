package org.unibl.etf.ip2024.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.unibl.etf.ip2024.models.dto.requests.CheckUsernameRequest;
import org.unibl.etf.ip2024.models.dto.requests.ResendEmailRequest;
import org.unibl.etf.ip2024.models.dto.response.JwtAuthenticationResponse;
import org.unibl.etf.ip2024.models.dto.requests.LoginRequest;
import org.unibl.etf.ip2024.models.dto.requests.SignUpRequest;
import org.unibl.etf.ip2024.services.AuthenticationService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authenticationService;

    // Endpoint for user signup
    @PostMapping("/signup")
    public ResponseEntity<JwtAuthenticationResponse> signup(@RequestBody SignUpRequest request) {
        JwtAuthenticationResponse response = this.authenticationService.signup(request);
        return ResponseEntity.ok(response);
    }

    // Endpoint for user login
    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(this.authenticationService.login(request));
    }

    // Endpoint for activating user account via token
    @GetMapping("/activate")
    public ResponseEntity<String> activateAccount(@RequestParam("token") String token) {
        boolean isActivated = this.authenticationService.activateAccount(token);
        if (isActivated) {
            return ResponseEntity.ok("Nalog je uspešno aktiviran!");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nevažeći ili istekao token.");
        }
    }

    // Endpoint for checking if a username is taken
    @PostMapping("/check-username")
    public ResponseEntity<Boolean> checkUsername(@RequestBody CheckUsernameRequest request) {
        boolean isTaken = authenticationService.checkUsername(request.getUsername());
        return ResponseEntity.ok(isTaken);
    }

    // Endpoint for resending activation email
    @PostMapping("/resend-email")
    public ResponseEntity<String> resendEmail(@RequestBody ResendEmailRequest request) {
        return authenticationService.resendEmail(request.getEmail(), request.getToken());
    }
}
