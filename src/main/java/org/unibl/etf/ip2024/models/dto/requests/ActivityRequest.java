package org.unibl.etf.ip2024.models.dto.requests;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ActivityRequest {
    private String activityType;
    private Integer duration;
    private String intensity;
    private BigDecimal result;
    private LocalDate logDate;
}
