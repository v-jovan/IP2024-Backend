package org.unibl.etf.ip2024.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.unibl.etf.ip2024.models.dto.ErrorResponse;
import org.unibl.etf.ip2024.services.JwtService;
import org.unibl.etf.ip2024.services.UserService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            final String authHeader = request.getHeader("Authorization");
            String jwt = null;
            String userEmail = null;

            if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
                jwt = authHeader.substring(7);
                userEmail = jwtService.extractUserName(jwt);
            }

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Obtain the UserDetailsService from UserService and then load UserDetails by username (email)
                UserDetails userDetails = userService.userDetailsService().loadUserByUsername(userEmail);

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    Claims claims = jwtService.extractAllClaims(jwt);
                    @SuppressWarnings("unchecked") // Suppresses unchecked assignment warning
                    List<String> roles = claims.get("roles", List.class); // Ensure the JWT service implementation provides this method to extract claims.
                    List<GrantedAuthority> authorities = roles.stream().map(SimpleGrantedAuthority::new) // Prefix ROLE_ if necessary, depending on how the roles are set up.
                            .collect(Collectors.toList());

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (JwtException e) {
            // Handle the exception and set a standardized error response
            handleException(request, response, e);
            return; // Skip further filter processing
        }

        filterChain.doFilter(request, response);
    }

    private void handleException(HttpServletRequest request, HttpServletResponse response, Exception e) throws IOException {
        ErrorResponse errorResponse = new ErrorResponse(HttpServletResponse.SC_FORBIDDEN, "JWT token nije validan ili je istekao", e.getMessage(), request.getRequestURI());
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

}