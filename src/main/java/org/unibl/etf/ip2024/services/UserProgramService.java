package org.unibl.etf.ip2024.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.unibl.etf.ip2024.models.dto.response.UserProgramResponse;

import java.security.Principal;

@Service
public interface UserProgramService {
    Page<UserProgramResponse> getUserPrograms(Principal principal, Pageable pageable);
    UserProgramResponse createUserProgram(Principal principal, Integer programId);
}
