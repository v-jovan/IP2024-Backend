package org.unibl.etf.ip2024.models.dto.response;

import lombok.Data;

import java.sql.Date;

@Data
public class UserProgramResponse {
    private Integer id;
    private String programName;
    private Date startDate;
    private Date endDate;
    private String status;
}
