package org.unibl.etf.ip2024.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.unibl.etf.ip2024.exceptions.AccountActivationException;
import org.unibl.etf.ip2024.exceptions.UserAlreadyExistsException;
import org.unibl.etf.ip2024.models.dto.CustomUserDetails;
import org.unibl.etf.ip2024.models.dto.response.JwtAuthenticationResponse;
import org.unibl.etf.ip2024.models.dto.requests.LoginRequest;
import org.unibl.etf.ip2024.models.dto.requests.SignUpRequest;
import org.unibl.etf.ip2024.models.entities.CityEntity;
import org.unibl.etf.ip2024.models.enums.Roles;
import org.unibl.etf.ip2024.models.entities.UserEntity;
import org.unibl.etf.ip2024.repositories.UserEntityRepository;
import org.unibl.etf.ip2024.services.*;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserEntityRepository userRepository; // Repository for user entities
    private final PasswordEncoder passwordEncoder; // Password encoder for encrypting passwords
    private final JwtService jwtService; // Service for handling JWT tokens
    private final UserService userService; // User service for loading user details
    private final CityService cityService; // City service
    private final AuthenticationManager authenticationManager; // Manager for authentication
    private final LogService logService; // Log service
    private final EmailService emailService;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Override
    public JwtAuthenticationResponse signup(SignUpRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Korisnik sa ovim emailom već postoji.");
        }

        UserEntity user = new UserEntity();
        user.setUsername(request.getUsername());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        CityEntity city = cityService.getCityById(request.getCityId()); // Get the city entity by ID
        user.setCity(city); // Set the user's city
        user.setPassword(passwordEncoder.encode(request.getPassword())); // Encrypt and set the user's password
        user.setRole(Roles.USER); // Set the user's role to USER
        user.setAvatarUrl(request.getAvatarUrl());
        userRepository.save(user); // Save the user entity to the database
        CustomUserDetails userDetails = new CustomUserDetails(
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                user.getAuthorities()
        );
        var jwt = jwtService.generateToken(userDetails); // Generate a JWT token for the user
        String activationLink = frontendUrl + "/auth/activate?token=" + jwt; // Generate an activation link
        emailService.sendActivationEmail(user.getEmail(), activationLink); // Send an activation email to the user

        logService.log(null, "Registracija korisnika " + user.getUsername() ); // Log the user registration

        return JwtAuthenticationResponse.builder().token(jwt).build(); // Return the JWT token
    }

    @Override
    public ResponseEntity<String> resendEmail(String email, String token) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserAlreadyExistsException("Korisnik sa ovim emailom ne postoji."));

        String activationLink = frontendUrl + "/auth/activate?token=" + token;
        emailService.sendActivationEmail(user.getEmail(), activationLink);

        logService.log(null, "Ponovno slanje emaila za aktivaciju korisniku " + user.getUsername() );

        return ResponseEntity.ok("Email je ponovo poslat.");
    }

    @Override
    public JwtAuthenticationResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmailOrUsername(), request.getPassword()));
        } catch (Exception ex) {
            throw new BadCredentialsException("Neispravni kredencijali");
        }
        UserDetails userDetails = userService.loadUserByUsername(request.getEmailOrUsername());
        var jwt = jwtService.generateToken(userDetails);

        logService.log(null, "Prijava korisnika " + request.getEmailOrUsername() );

        return JwtAuthenticationResponse.builder().token(jwt).build();
    }

    @Override
    public boolean activateAccount(String token) {
        // Extract email from the token
        String email = jwtService.extractUserEmail(token);

        // Find user by email
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AccountActivationException("Neispravan token!"));

        // Validate the token
        if (jwtService.isTokenValid(token, user)) {
            // Activate the user account
            user.setActivated(true);
            userRepository.saveAndFlush(user);
            return true; // Return true if activation is successful
        }

        // If the token is invalid or expired
        throw new AccountActivationException("Token je nevažeći ili je istekao.");
    }

    @Override
    public boolean checkUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
}
