package org.unibl.etf.ip2024.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.unibl.etf.ip2024.exceptions.InvalidTokenException;
import org.unibl.etf.ip2024.exceptions.UserAlreadyExistsException;
import org.unibl.etf.ip2024.models.dto.CustomUserDetails;
import org.unibl.etf.ip2024.models.dto.response.JwtAuthenticationResponse;
import org.unibl.etf.ip2024.models.dto.requests.LoginRequest;
import org.unibl.etf.ip2024.models.dto.requests.SignUpRequest;
import org.unibl.etf.ip2024.models.entities.CityEntity;
import org.unibl.etf.ip2024.models.enums.Roles;
import org.unibl.etf.ip2024.models.entities.UserEntity;
import org.unibl.etf.ip2024.repositories.UserEntityRepository;
import org.unibl.etf.ip2024.services.AuthenticationService;
import org.unibl.etf.ip2024.services.CityService;
import org.unibl.etf.ip2024.services.JwtService;
import org.unibl.etf.ip2024.services.UserService;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserEntityRepository userRepository; // Repository for user entities
    private final PasswordEncoder passwordEncoder; // Password encoder for encrypting passwords
    private final JwtService jwtService; // Service for handling JWT tokens
    private final UserService userService; // User service for loading user details
    private final CityService cityService; // City service
    private final AuthenticationManager authenticationManager; // Manager for authentication

    @Override
    public JwtAuthenticationResponse signup(SignUpRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Korisnik sa ovim emailom već postoji.");
        }

        UserEntity user = new UserEntity(); // Create a new user entity
        user.setUsername(request.getEmail()); // Set the username to the email
        user.setFirstName(request.getFirstName()); // Set the user's first name
        user.setLastName(request.getLastName()); // Set the user's last name
        user.setEmail(request.getEmail()); // Set the user's email
        CityEntity city = cityService.getCityById(request.getCityId()); // Get the city entity by ID
        user.setCity(city); // Set the user's city
        user.setPassword(passwordEncoder.encode(request.getPassword())); // Encrypt and set the user's password
        user.setRole(Roles.USER); // Set the user's role to USER
        user.setAvatarUrl(request.getAvatarUrl()); // Set the user's avatar URL
        userRepository.save(user); // Save the user entity to the database
        CustomUserDetails userDetails = new CustomUserDetails(
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                user.getAuthorities()
        );
        var jwt = jwtService.generateToken(userDetails); // Generate a JWT token for the user
        return JwtAuthenticationResponse.builder().token(jwt).build(); // Return the JWT token
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
        return JwtAuthenticationResponse.builder().token(jwt).build();
    }

    @Override
    public boolean activateAccount(String token) {
        // Extract email from the token
        String email = jwtService.extractUserEmail(token);

        // Find user by email
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidTokenException("Neispravan token!"));

        // Validate the token
        if (jwtService.isTokenValid(token, user)) {
            // Activate the user account
//            user.setActive(true);
//            userRepository.save(user);
            return true; // Return true if activation is successful
        }

        // If the token is invalid or expired
        throw new InvalidTokenException("Token je nevažeći ili je istekao.");
    }

    @Override
    public boolean checkUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
}
