package org.unibl.etf.ip2024.models.dto.requests;

import lombok.Data;

@Data
public class UpdatePasswordRequest {
    private String oldPassword;
    private String newPassword;
}