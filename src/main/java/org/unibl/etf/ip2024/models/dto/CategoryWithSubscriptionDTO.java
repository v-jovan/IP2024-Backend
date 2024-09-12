package org.unibl.etf.ip2024.models.dto;

import lombok.Data;

@Data
public class CategoryWithSubscriptionDTO {
    private Integer id;
    private String name;
    private String description;
    private Boolean subscribed;
}
