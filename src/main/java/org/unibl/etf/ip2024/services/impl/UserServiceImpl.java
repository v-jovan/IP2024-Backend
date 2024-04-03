package org.unibl.etf.ip2024.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.unibl.etf.ip2024.models.entities.UserEntity;
import org.unibl.etf.ip2024.repositories.UserEntityRepository;
import org.unibl.etf.ip2024.services.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserEntityRepository userRepository;

    @Override
    public UserDetailsService userDetailsService() {
        return email -> {
            UserEntity user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Korisnik nije pronadjen: " + email));

            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

            return new User(user.getEmail(), user.getPassword(), authorities);
        };
    }
}