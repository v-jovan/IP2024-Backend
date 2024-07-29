package org.unibl.etf.ip2024.models.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Integer id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private String avatarUrl;
    private String biography;
    private String cityName;

}