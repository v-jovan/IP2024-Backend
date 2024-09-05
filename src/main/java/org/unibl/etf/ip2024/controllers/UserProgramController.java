package org.unibl.etf.ip2024.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.unibl.etf.ip2024.models.dto.response.UserProgramResponse;
import org.unibl.etf.ip2024.services.UserProgramService;

import java.security.Principal;

@RestController
@RequestMapping("/user-programs")
@RequiredArgsConstructor
public class UserProgramController {

    private final UserProgramService userProgramService;

    @GetMapping
    public ResponseEntity<Page<UserProgramResponse>> getUserPrograms(
            Principal principal,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserProgramResponse> userPrograms = userProgramService.getUserPrograms(principal, pageable);
        return ResponseEntity.ok(userPrograms);
    }

    @PostMapping("/{programId}")
    public ResponseEntity<UserProgramResponse> createUserProgram(
            Principal principal,
            @PathVariable Integer programId) {
        UserProgramResponse userProgram = userProgramService.createUserProgram(principal, programId);
        return ResponseEntity.ok(userProgram);
    }
}
