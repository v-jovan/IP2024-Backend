package org.unibl.etf.ip2024.services;

import org.springframework.stereotype.Service;
import org.unibl.etf.ip2024.models.dto.Exercise;

import java.io.IOException;

@Service
public interface DailyExerciseService {
    Exercise[] getDailyExercises() throws IOException;
}
