package org.unibl.etf.ip2024.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.unibl.etf.ip2024.models.dto.AdviserDTO;
import org.unibl.etf.ip2024.models.dto.requests.UpdatePasswordRequest;
import org.unibl.etf.ip2024.models.dto.requests.UpdateUserRequest;
import org.unibl.etf.ip2024.models.dto.response.NonAdvisersResponse;
import org.unibl.etf.ip2024.models.dto.response.UserInfoResponse;
import org.unibl.etf.ip2024.services.UserService;

import java.security.Principal;
import java.util.List;

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

    @GetMapping("/info-id")
    public ResponseEntity<UserInfoResponse> getUserInfoById(@RequestParam("id") Integer id) {
        return ResponseEntity.ok(this.userService.getUserInfoById(id));
    }

    @GetMapping("/user-id")
    public ResponseEntity<Integer> getUserId(@RequestParam("username") String username) {
        return ResponseEntity.ok(userService.getUserId(username));
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

    @GetMapping("/avatar")
    public ResponseEntity<String> getAvatar(Principal principal) {
        String username = principal.getName();
        return ResponseEntity.ok(this.userService.getAvatar(username));
    }

    @GetMapping("/active")
    public ResponseEntity<Boolean> isActive(Principal principal) {
        String username = principal.getName();
        return ResponseEntity.ok(this.userService.isActive(username));
    }

    @GetMapping("/advisers")
    public ResponseEntity<List<AdviserDTO>> getAdvisers() {
        return ResponseEntity.ok(userService.getAllAdvisers());
    }

    @GetMapping("/non-advisers")
    public ResponseEntity<List<NonAdvisersResponse>> getNonAdvisers(Principal principal) {
        return ResponseEntity.ok(userService.getAllNonAdvisers(principal));
    }
}
