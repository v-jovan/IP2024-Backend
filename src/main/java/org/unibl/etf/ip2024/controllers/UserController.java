package org.unibl.etf.ip2024.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.unibl.etf.ip2024.models.dto.requests.UpdatePasswordRequest;
import org.unibl.etf.ip2024.models.dto.requests.UpdateUserRequest;
import org.unibl.etf.ip2024.models.dto.response.UserInfoResponse;
import org.unibl.etf.ip2024.services.UserService;

import java.security.Principal;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/info")
    public ResponseEntity<UserInfoResponse> getUserInfo(Principal principal) {
        String username = principal.getName();
        return ResponseEntity.ok(this.userService.getUserInfo(username));
    }

    @PatchMapping("/info")
    public ResponseEntity<UserInfoResponse> updateUserInfo(Principal principal, @RequestBody UpdateUserRequest updateUserRequest) {
        String username = principal.getName();
        UserInfoResponse updatedUserInfo = this.userService.updateUserInfo(username, updateUserRequest);
        return ResponseEntity.ok(updatedUserInfo);
    }

    @PatchMapping("/password")
    public ResponseEntity<String> updatePassword(Principal principal, @RequestBody UpdatePasswordRequest updatePasswordRequest) {
        String username = principal.getName();
        this.userService.updatePassword(username, updatePasswordRequest);
        return ResponseEntity.ok("Lozinka uspješno promijenjena!");
    }
}
