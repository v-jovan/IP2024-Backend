package org.unibl.etf.ip2024.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.unibl.etf.ip2024.exceptions.*;
import org.unibl.etf.ip2024.models.dto.requests.FitnessProgramRequest;
import org.unibl.etf.ip2024.models.dto.response.FitnessProgramResponse;
import org.unibl.etf.ip2024.models.entities.*;
import org.unibl.etf.ip2024.repositories.*;
import org.unibl.etf.ip2024.services.FitnessProgramService;
import org.unibl.etf.ip2024.services.ImageUploadService;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FitnessProgramServiceImpl implements FitnessProgramService {

    private final FitnessProgramEntityRepository fitnessProgramRepository;
    private final CategoryEntityRepository categoryRepository;
    private final UserEntityRepository userRepository;
    private final LocationEntityRepository locationRepository;
    private final ProgramImageEntityRepository programImageRepository;
    private final ImageUploadService imageUploadService;
    private final ProgramAttributeEntityRepository programAttributeRepository;
    private final AttributeValueEntityRepository attributeValueRepository;

    @Override
    @Transactional
    public FitnessProgramResponse addFitnessProgram(Principal principal, FitnessProgramRequest fitnessProgramRequest, List<MultipartFile> files) throws IOException {

        // Check if program with the same name already exists
        Optional<FitnessProgramEntity> existingProgram = fitnessProgramRepository.findByName(fitnessProgramRequest.getName());
        if (existingProgram.isPresent()) {
            throw new ProgramAlreadyExistsException("Program sa imenom '" + fitnessProgramRequest.getName() + "' već postoji.");
        }

        if ((fitnessProgramRequest.getYoutubeUrl() != null && fitnessProgramRequest.getLocationId() != null) ||
                (fitnessProgramRequest.getYoutubeUrl() == null && fitnessProgramRequest.getLocationId() == null)) {
            throw new IllegalArgumentException("Program mora biti online ili offline.");
        }

        // Create program
        FitnessProgramEntity fitnessProgramEntity = new FitnessProgramEntity();
        fitnessProgramEntity.setDuration(fitnessProgramRequest.getDuration());
        fitnessProgramEntity.setName(fitnessProgramRequest.getName());
        fitnessProgramEntity.setPrice(fitnessProgramRequest.getPrice());
        fitnessProgramEntity.setDifficultyLevel(fitnessProgramRequest.getDifficultyLevel());
        fitnessProgramEntity.setDescription(fitnessProgramRequest.getDescription());
        fitnessProgramEntity.setYoutubeUrl(fitnessProgramRequest.getYoutubeUrl());

        // Category, user, location
        CategoryEntity category = categoryRepository.findById(fitnessProgramRequest.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException("Kategorija nije pronađena"));
        fitnessProgramEntity.setCategory(category);

        UserEntity user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("Korisnik nije pronađen"));
        fitnessProgramEntity.setUser(user);

        if (fitnessProgramRequest.getLocationId() != null) {
            LocationEntity location = locationRepository.findById(fitnessProgramRequest.getLocationId())
                    .orElseThrow(() -> new LocationNotFoundException("Lokacija nije pronađena"));
            fitnessProgramEntity.setLocation(location);
        }

        FitnessProgramEntity savedProgram = fitnessProgramRepository.saveAndFlush(fitnessProgramEntity);

        // Images
        if (files != null && !files.isEmpty()) {
            List<ProgramImageEntity> programImages = new ArrayList<>();
            for (MultipartFile file : files) {
                String fileName = imageUploadService.uploadImage(file);
                String imageUrl = "/uploads/" + fileName;

                ProgramImageEntity programImage = new ProgramImageEntity();
                programImage.setFitnessProgram(savedProgram);
                programImage.setImageUrl(imageUrl);
                programImages.add(programImage);
                programImageRepository.save(programImage);
            }
            savedProgram.setProgramImages(programImages);
        }

        // Specific attributes
        if (fitnessProgramRequest.getSpecificAttributes() != null && !fitnessProgramRequest.getSpecificAttributes().isEmpty()) {
            List<ProgramAttributeEntity> programAttributes = new ArrayList<>();
            for (FitnessProgramRequest.SpecificAttribute attribute : fitnessProgramRequest.getSpecificAttributes()) {
                ProgramAttributeEntity programAttributeEntity = new ProgramAttributeEntity();

                AttributeValueEntity attributeValueEntity = attributeValueRepository.findById(attribute.getAttributeValue())
                        .orElseThrow(() -> new AttributeValueNotFoundException("Vrijednost atributa nije pronađena"));

                programAttributeEntity.setFitnessProgram(savedProgram);
                programAttributeEntity.setAttributeValue(attributeValueEntity);

                programAttributes.add(programAttributeEntity);
                programAttributeRepository.save(programAttributeEntity);
            }
            savedProgram.setProgramAttributes(programAttributes);
        }

        return new FitnessProgramResponse(savedProgram.getId());
    }
}
