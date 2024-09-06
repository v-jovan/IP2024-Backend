package org.unibl.etf.ip2024.models.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.unibl.etf.ip2024.models.dto.AttributeDTO;
import org.unibl.etf.ip2024.models.enums.DifficultyLevel;
import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FitnessProgramResponse {
    private Integer id;
    private String name;
    private String description;
    private Integer duration;
    private BigDecimal price;
    private DifficultyLevel difficultyLevel;
    private String youtubeUrl;
    private Integer locationId;
    private String locationName;
    private Integer categoryId;
    private String categoryName;
    private String instructorName;
    private Integer instructorId;
    private List<AttributeDTO> specificAttributes;
    private List<String> images;

    public FitnessProgramResponse(Integer id) {
        this.id = id;
    }
}
