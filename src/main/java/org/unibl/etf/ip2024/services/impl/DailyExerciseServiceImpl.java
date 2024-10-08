package org.unibl.etf.ip2024.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.unibl.etf.ip2024.exceptions.ExerciseFetchException;
import org.unibl.etf.ip2024.models.dto.ExerciseDTO;
import org.unibl.etf.ip2024.services.DailyExerciseService;
import org.unibl.etf.ip2024.services.LogService;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class DailyExerciseServiceImpl implements DailyExerciseService {
    @Value("${apininja.api.url}")
    private String apiUrl;
    @Value("${apininja.api.key}")
    private String apiKey;

    private final LogService logService;

    public DailyExerciseServiceImpl(LogService logService) {
        this.logService = logService;
    }

    @Override
    public ExerciseDTO[] getDailyExercises() throws IOException {
        // this is the recommended way to handle the connection to api-ninjas... for whatever reason
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("accept", "application/json");
        connection.setRequestProperty("X-Api-Key", apiKey);

        logService.log(null, "Prikaz dnevnih vježbi");

        try (InputStream responseStream = connection.getInputStream()) {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(responseStream, ExerciseDTO[].class);
        } catch (Exception e) {
            throw new ExerciseFetchException("Greška u komunikaciji sa API-jem: " + e.getMessage(), e);
        }
    }
}
