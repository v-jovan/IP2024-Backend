package org.unibl.etf.ip2024.models.dto;

import lombok.Data;
import org.unibl.etf.ip2024.models.entities.UserEntity;

import java.util.List;

@Data
public class AdvisorDTO {
    private Integer id;
    private String name;
    private String email;

    public static List<AdvisorDTO> fromEntities(List<UserEntity> advisors) {
        return advisors.stream().map(advisor -> {
            AdvisorDTO advisorDTO = new AdvisorDTO();
            advisorDTO.setId(advisor.getId());
            advisorDTO.setName(userName(advisor));
            advisorDTO.setEmail(advisor.getEmail());
            return advisorDTO;
        }).toList();
    }

    private static String userName(UserEntity advisor) {
        if (advisor == null) {
            return "";
        }
        String displayName;
        if (advisor.getFirstName() != null && advisor.getLastName() != null) {
            displayName = advisor.getFirstName() + " " + advisor.getLastName();
        } else if (advisor.getFirstName() != null) {
            displayName = advisor.getFirstName();
        } else if (advisor.getLastName() != null) {
            displayName = advisor.getLastName();
        } else {
            displayName = advisor.getUsername();
        }

        return displayName;
    }
}
