package org.unibl.etf.ip2024.models.enums;

import lombok.Getter;

@Getter
public enum DifficultyLevel {
    BEGINNER(1),
    INTERMEDIATE(2),
    ADVANCED(3);

    private final int order;

    DifficultyLevel(int order) {
        this.order = order;
    }

}
