package org.unibl.etf.ip2024.services;

import org.springframework.stereotype.Service;
import org.unibl.etf.ip2024.models.dto.JwtAuthenticationResponse;
import org.unibl.etf.ip2024.models.dto.LoginRequest;
import org.unibl.etf.ip2024.models.dto.SignUpRequest;

@Service
public interface AuthenticationService {
    JwtAuthenticationResponse signup(SignUpRequest request);

    JwtAuthenticationResponse login(LoginRequest request);
}