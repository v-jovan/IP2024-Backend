package org.unibl.etf.ip2024.models.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.unibl.etf.ip2024.models.enums.DifficultyLevel;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FitnessProgramListResponse {
    private Integer id;
    private String name;
    private String description;
    private Integer duration;
    private BigDecimal price;
    private DifficultyLevel difficultyLevel;
    private String youtubeUrl;
    private String locationName;
}
