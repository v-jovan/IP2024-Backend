package org.unibl.etf.ip2024.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.unibl.etf.ip2024.models.dto.ExerciseDTO;
import org.unibl.etf.ip2024.services.DailyExerciseService;

import java.io.IOException;

@RestController
@RequestMapping("/daily-exercises")
@RequiredArgsConstructor
public class DailyExercisesController {
    private final DailyExerciseService dailyExerciseService;

    // Endpoint to get daily exercises
    @GetMapping
    public ExerciseDTO[] getDailyExercises() throws IOException {
        return this.dailyExerciseService.getDailyExercises();
    }
}
