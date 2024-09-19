package org.unibl.etf.ip2024.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.unibl.etf.ip2024.models.dto.response.JwtAuthenticationResponse;
import org.unibl.etf.ip2024.models.dto.requests.LoginRequest;
import org.unibl.etf.ip2024.models.dto.requests.SignUpRequest;

@Service
public interface AuthenticationService {
    JwtAuthenticationResponse signup(SignUpRequest request);
    ResponseEntity<String> resendEmail(String email, String token);
    JwtAuthenticationResponse login(LoginRequest request);
    boolean activateAccount(String token);
    boolean checkUsername(String username);
}