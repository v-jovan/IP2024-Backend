package org.unibl.etf.ip2024.models.dto;

import lombok.Data;

import java.util.List;

@Data
public class AttributeDTO {
    private Integer id;
    private String name;
    private String description;
    private List<AttributeValueDTO> values;
}