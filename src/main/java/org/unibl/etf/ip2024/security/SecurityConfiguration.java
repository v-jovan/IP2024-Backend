package org.unibl.etf.ip2024.security;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.unibl.etf.ip2024.services.UserService;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration // Marks this class as a configuration class
@EnableWebSecurity // Enables Spring Security for the application
@EnableMethodSecurity // Enables method-level security
@RequiredArgsConstructor // Annotation to generate a constructor with final fields
public class SecurityConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfiguration.class); // Logger for logging information
    private final JwtAuthenticationFilter jwtAuthenticationFilter; // JWT filter for authentication
    private final UserService userService; // User service to manage user details
    private final CorsConfig corsConfig; // CORS configuration bean
    private final PasswordEncoder passwordEncoder; // Password encoder bean

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Configuring Security Filter Chain");
        http
                .cors(cors -> {
                    cors.configurationSource(corsConfig.corsConfigurationSource()); // Sets the CORS configuration source
                    logger.info("CORS configuration applied");
                })
                .csrf(AbstractHttpConfigurer::disable) // Disables CSRF protection because... I don't need it right now :D
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/auth/**").permitAll() // Allows unauthenticated access to /auth/**
                        .requestMatchers("/upload/**").permitAll() // Allows unauthenticated access to /upload/**
                        .requestMatchers("/uploads/**").permitAll() // Allows unauthenticated access to /uploads/**
                        .requestMatchers("/test/**").permitAll() // Allows unauthenticated access to /test/**
                        .requestMatchers("/cities/**").permitAll() // Allows unauthenticated access to /cities/**
                        .requestMatchers("/news/**").permitAll() // Allows unauthenticated access to /cities/**
                        .anyRequest().authenticated()) // Requires authentication for any other requests
                .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS)) // Configures session management to be stateless (jwt)
                .authenticationProvider(authenticationProvider()) // Sets the authentication provider
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // Adds JWT filter before UsernamePasswordAuthenticationFilter
        logger.info("Security Filter Chain configured successfully");
        return http.build(); // Builds the security filter chain
    }

//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        logger.info("Creating BCryptPasswordEncoder bean");
//        return new BCryptPasswordEncoder(); // Returns a BCrypt password encoder
//    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        logger.info("Configuring DaoAuthenticationProvider");
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService); // Sets the user details service
        authProvider.setPasswordEncoder(passwordEncoder); // Sets the password encoder
        logger.info("DaoAuthenticationProvider configured successfully");
        return authProvider; // Returns the authentication provider
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        logger.info("Creating AuthenticationManager bean");
        return config.getAuthenticationManager(); // Retrieves and returns the authentication manager
    }
}
