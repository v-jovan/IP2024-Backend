package org.unibl.etf.ip2024.services;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.unibl.etf.ip2024.models.dto.CategoryDTO;
import org.unibl.etf.ip2024.models.dto.requests.FitnessProgramRequest;
import org.unibl.etf.ip2024.models.dto.response.FitnessProgramHomeResponse;
import org.unibl.etf.ip2024.models.dto.response.FitnessProgramListResponse;
import org.unibl.etf.ip2024.models.dto.response.FitnessProgramResponse;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Service
public interface FitnessProgramService {
    @Transactional
    FitnessProgramResponse addFitnessProgram(Principal principal, FitnessProgramRequest fitnessProgramRequest, List<MultipartFile> files) throws IOException;

    @Transactional
    Page<FitnessProgramListResponse> getMyFitnessPrograms(Principal principal, Pageable pageable);

    @Transactional
    Page<FitnessProgramHomeResponse> getAllFitnessPrograms(Pageable pageable);

    FitnessProgramResponse getFitnessProgram(Integer id);

    @Transactional
    FitnessProgramResponse updateFitnessProgram(Integer programId, FitnessProgramRequest fitnessProgramRequest, List<MultipartFile> files, List<String> removedImages) throws IOException;

    Page<FitnessProgramHomeResponse> getAllFitnessProgramsByAttributeValue(Integer attributeValueId, Pageable pageable);

    Page<FitnessProgramHomeResponse> getAllFitnessProgramsByAttributeId(Integer attributeId, Pageable pageable);

    Page<FitnessProgramHomeResponse> getAllFitnessProgramsByCategoryId(Integer categoryId, Pageable pageable);

    List<CategoryDTO> getAllCategoriesWithAttributesAndValues();

    @Transactional
    void deleteFitnessProgram(Integer programId, Principal principal) throws IOException;

}
