package org.unibl.etf.ip2024.services;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.unibl.etf.ip2024.models.dto.AdvisorDTO;
import org.unibl.etf.ip2024.models.dto.requests.UpdatePasswordRequest;
import org.unibl.etf.ip2024.models.dto.requests.UpdateUserRequest;
import org.unibl.etf.ip2024.models.dto.response.NonAdvisorsResponse;
import org.unibl.etf.ip2024.models.dto.response.UserInfoResponse;

import java.security.Principal;
import java.util.List;

@Service
public interface UserService extends UserDetailsService  {
    UserInfoResponse getUserInfo(String username);
    UserInfoResponse updateUserInfo(String username, UpdateUserRequest userInfoResponse);
    void updatePassword(String username, UpdatePasswordRequest updatePasswordRequest);
    String getAvatar(String username);
    Boolean isActive(String username);
    Integer getUserId(String username);
    UserInfoResponse getUserInfoById(Integer id);
    List<AdvisorDTO> getAllAdvisors();
    List<NonAdvisorsResponse> getAllNonAdvisors(Principal principal);
}
