package org.unibl.etf.ip2024.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.unibl.etf.ip2024.models.entities.UserEntity;
import org.unibl.etf.ip2024.repositories.UserEntityRepository;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserEntityRepository userEntityRepository;

    public UserController(UserEntityRepository userEntityRepository) {
        this.userEntityRepository = userEntityRepository;
    }

    @GetMapping
    List<UserEntity> findAll() {
        return userEntityRepository.findAll();
    }
}
