package org.unibl.etf.ip2024.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data               // Generates getters, setters, toString, equals, and hashCode methods
@NoArgsConstructor  // Generates a no-argument constructor
@AllArgsConstructor // Generates a constructor with one argument for each field in the class
public class ErrorResponse {
    private int status;
    private String error;
    private String message;
    private String path;
}
