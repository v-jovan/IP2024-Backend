package org.unibl.etf.ip2024.services;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.unibl.etf.ip2024.models.dto.requests.FitnessProgramRequest;
import org.unibl.etf.ip2024.models.dto.response.FitnessProgramResponse;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Service
public interface FitnessProgramService {
    @Transactional
    FitnessProgramResponse addFitnessProgram(Principal principal, FitnessProgramRequest fitnessProgramRequest, List<MultipartFile> files) throws IOException;
}
