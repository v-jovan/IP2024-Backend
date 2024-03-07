package org.unibl.etf.ip2024.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    public AuthController() {
    }

    @GetMapping("/test")
    public String test() {
        return "Test";
    }
}
