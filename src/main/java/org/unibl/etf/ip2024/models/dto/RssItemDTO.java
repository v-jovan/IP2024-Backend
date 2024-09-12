package org.unibl.etf.ip2024.models.dto;

import lombok.Data;

@Data
public class RssItemDTO {
    private String category;
    private String title;
    private String link;
    private String description;

}