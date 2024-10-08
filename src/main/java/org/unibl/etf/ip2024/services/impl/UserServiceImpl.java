package org.unibl.etf.ip2024.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.unibl.etf.ip2024.exceptions.InvalidOldPasswordException;
import org.unibl.etf.ip2024.exceptions.UserNotFoundException;
import org.unibl.etf.ip2024.models.dto.AdviserDTO;
import org.unibl.etf.ip2024.models.dto.CustomUserDetails;
import org.unibl.etf.ip2024.models.dto.requests.UpdatePasswordRequest;
import org.unibl.etf.ip2024.models.dto.requests.UpdateUserRequest;
import org.unibl.etf.ip2024.models.dto.response.NonAdvisersResponse;
import org.unibl.etf.ip2024.models.dto.response.UserInfoResponse;
import org.unibl.etf.ip2024.models.entities.UserEntity;
import org.unibl.etf.ip2024.models.enums.Roles;
import org.unibl.etf.ip2024.repositories.UserEntityRepository;
import org.unibl.etf.ip2024.services.LogService;
import org.unibl.etf.ip2024.services.UserService;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserEntityRepository userRepository;
    private final CityServiceImpl cityService;
    private final PasswordEncoder passwordEncoder;
    private final LogService logService;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        // Check for user by username
        Optional<UserEntity> userOptional = userRepository.findByUsername(usernameOrEmail);

        if (userOptional.isEmpty()) {
            // If not found by username, check by email
            userOptional = userRepository.findByEmail(usernameOrEmail);
        }

        UserEntity user = userOptional.orElseThrow(() -> new UsernameNotFoundException("Korisnik nije pronadjen: " + usernameOrEmail));
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRole().name()));
        logService.log(null, "Dohvatanje korisnika");
        return new CustomUserDetails(user.getUsername(), user.getPassword(), user.getEmail(), authorities);
    }

    @Override
    public UserInfoResponse getUserInfo(String username) {
        UserEntity user = userRepository.findByUsername(username).
                orElseThrow(() -> new UsernameNotFoundException("Korisnik nije pronadjen: " + username));

        logService.log(null, "Prikaz informacija o korisniku");

        return new UserInfoResponse(
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getAvatarUrl(),
                user.getBiography(),
                user.getCity().getId()
        );
    }

    @Override
    public UserInfoResponse updateUserInfo(String username, UpdateUserRequest updateUserRequest) {
        UserEntity user = userRepository.findByUsername(username).
                orElseThrow(() -> new UsernameNotFoundException("Korisnik nije pronadjen: " + username));

        user.setAvatarUrl(updateUserRequest.getAvatarUrl());

        if (updateUserRequest.getFirstName() != null && !updateUserRequest.getFirstName().equals(user.getFirstName())) {
            user.setFirstName(updateUserRequest.getFirstName());
        }
        if (updateUserRequest.getLastName() != null && !updateUserRequest.getLastName().equals(user.getLastName())) {
            user.setLastName(updateUserRequest.getLastName());
        }
        if (updateUserRequest.getEmail() != null && !updateUserRequest.getEmail().equals(user.getEmail())) {
            user.setEmail(updateUserRequest.getEmail());
        }
        if (updateUserRequest.getBiography() != null && !updateUserRequest.getBiography().equals(user.getBiography())) {
            user.setBiography(updateUserRequest.getBiography());
        }
        if (updateUserRequest.getCityId() != null && !updateUserRequest.getCityId().equals(user.getCity().getId())) {
            user.setCity(cityService.getCityById(updateUserRequest.getCityId()));
        }

        logService.log(null, "Ažuriranje informacija o korisniku");
        userRepository.save(user);

        return new UserInfoResponse(
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getAvatarUrl(),
                user.getBiography(),
                user.getCity().getId()
        );
    }

    @Override
    public void updatePassword(String username, UpdatePasswordRequest updatePasswordRequest) {
        UserEntity user = userRepository.findByUsername(username).
                orElseThrow(() -> new UsernameNotFoundException("Korisnik nije pronadjen: " + username));

        if (!passwordEncoder.matches(updatePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new InvalidOldPasswordException("Stara lozinka nije ispravna!");
        }

        user.setPassword(passwordEncoder.encode(updatePasswordRequest.getNewPassword()));
        logService.log(null, "Ažuriranje lozinke");
        userRepository.saveAndFlush(user);
    }

    @Override
    public String getAvatar(String username) {
        UserEntity user = userRepository.findByUsername(username).
                orElseThrow(() -> new UsernameNotFoundException("Korisnik nije pronadjen: " + username));

        return user.getAvatarUrl();
    }

    @Override
    public Boolean isActive(String username) {
        UserEntity user = userRepository.findByUsername(username).
                orElseThrow(() -> new UsernameNotFoundException("Korisnik nije pronadjen: " + username));

        return user.isActivated();
    }

    @Override
    public Integer getUserId(String username) {
        UserEntity user = this.userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Korisnik sa ovim emailom ne postoji."));
        return user.getId();
    }

    @Override
    public UserInfoResponse getUserInfoById(Integer id) {

        UserEntity user = userRepository.findById(id).
                orElseThrow(() -> new UsernameNotFoundException("Korisnik nije pronađen: "));

        return new UserInfoResponse(
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getAvatarUrl(),
                user.getBiography(),
                user.getCity().getId()
        );
    }

    @Override
    public List<AdviserDTO> getAllAdvisers() {
        List<UserEntity> advisers = userRepository.findAllByRole(Roles.INSTRUCTOR);
        if (advisers.isEmpty()) {
            throw new UserNotFoundException("Nema savjetnika u sistemu.");
        }
        return advisers.stream()
                .map(this::mapToAdviserDTO)
                .collect(Collectors.toList());
    }


    @Override
    public List<NonAdvisersResponse> getAllNonAdvisers(Principal principal) {

        UserEntity user = userRepository.findByUsername(principal.getName())
                .orElseThrow(UserNotFoundException::new);

        List<UserEntity> users = userRepository.findAllByRoleNotAndUsernameNot(Roles.INSTRUCTOR, user.getUsername());

        if (users.isEmpty()) {
            throw new UserNotFoundException("Nema korisnika u sistemu.");
        }

        return users.stream()
                .map(this::mapToNonAdviser)
                .collect(Collectors.toList());
    }


    private NonAdvisersResponse mapToNonAdviser(UserEntity userEntity) {
        NonAdvisersResponse response = new NonAdvisersResponse();
        response.setUserId(userEntity.getId());
        response.setName(getDisplayName(userEntity));

        return response;
    }

    private AdviserDTO mapToAdviserDTO(UserEntity adviser) {
        AdviserDTO adviserDTO = new AdviserDTO();
        adviserDTO.setId(adviser.getId());
        adviserDTO.setName(getDisplayName(adviser));
        adviserDTO.setEmail(adviser.getEmail());
        return adviserDTO;
    }

    private static String getDisplayName(UserEntity user) {
        if (user == null) {
            return "";
        }
        String displayName;
        if (user.getFirstName() != null && user.getLastName() != null) {
            displayName = user.getFirstName() + " " + user.getLastName();
        } else if (user.getFirstName() != null) {
            displayName = user.getFirstName();
        } else if (user.getLastName() != null) {
            displayName = user.getLastName();
        } else {
            displayName = user.getUsername();
        }
        return displayName;
    }
}