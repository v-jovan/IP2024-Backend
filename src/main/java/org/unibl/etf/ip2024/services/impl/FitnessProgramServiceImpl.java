package org.unibl.etf.ip2024.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.unibl.etf.ip2024.exceptions.*;
import org.unibl.etf.ip2024.models.dto.AttributeDTO;
import org.unibl.etf.ip2024.models.dto.AttributeValueDTO;
import org.unibl.etf.ip2024.models.dto.requests.FitnessProgramRequest;
import org.unibl.etf.ip2024.models.dto.response.FitnessProgramListResponse;
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
import java.util.stream.Collectors;

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

        if ((fitnessProgramRequest.getLocationId() == null && fitnessProgramRequest.getYoutubeUrl() == null) ||
                (fitnessProgramRequest.getLocationId() != null && fitnessProgramRequest.getYoutubeUrl() != null)) {
            throw new IllegalArgumentException("Program mora biti ili online ili offline, ali ne oba.");
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

    @Override
    @Transactional
    public Page<FitnessProgramListResponse> getFitnessPrograms(Principal principal, Pageable pageable) {
        Page<FitnessProgramEntity> programs;
        if (principal != null) {
            UserEntity user = userRepository.findByUsername(principal.getName())
                    .orElseThrow(() -> new UserNotFoundException("Korisnik nije pronađen"));
            programs = fitnessProgramRepository.findAllByUserId(user.getId(), pageable);
        } else {
            programs = fitnessProgramRepository.findAll(pageable);
        }

        return programs.map(this::getFitnessProgramListResponse);
    }

    @Override
    public FitnessProgramResponse getFitnessProgram(Integer id) {

        FitnessProgramEntity programEntity = fitnessProgramRepository.findById(id)
                .orElseThrow(() -> new ProgramNotFoundException("Program sa ID-jem " + id + " nije pronađen."));

        // Create a new FitnessPRogramDTO
        FitnessProgramResponse response = new FitnessProgramResponse();

        // Set the fields in the DTO
        response.setId(programEntity.getId());
        response.setName(programEntity.getName());
        response.setDescription(programEntity.getDescription());
        response.setDuration(programEntity.getDuration());
        response.setPrice(programEntity.getPrice());
        response.setDifficultyLevel(programEntity.getDifficultyLevel());
        response.setYoutubeUrl(programEntity.getYoutubeUrl());

        // Map the location
        if (programEntity.getLocation() != null) {
            response.setLocationId(programEntity.getLocation().getId());
        }

        // Map the category
        response.setCategoryId(programEntity.getCategory().getId());

        // Map the specific attributes
        List<AttributeDTO> specificAttributes = programEntity
                .getProgramAttributes()
                .stream()
                .map(this::getAttributeDTO)
                .collect(Collectors.toList());
        response.setSpecificAttributes(specificAttributes);

        List<String> imageUrls = programEntity
                .getProgramImages()
                .stream()
                .map(ProgramImageEntity::getImageUrl)
                .collect(Collectors.toList());
        response.setImages(imageUrls);

        return response;
    }

    @Override
    public FitnessProgramResponse updateFitnessProgram(Integer programId, FitnessProgramRequest fitnessProgramRequest, List<MultipartFile> files, List<String> removedImages) throws IOException {
        FitnessProgramEntity fitnessProgramEntity = fitnessProgramRepository.findById(programId)
                .orElseThrow(() -> new ProgramNotFoundException("Program sa ID-jem " + programId + " nije pronađen."));

        fitnessProgramEntity.setName(fitnessProgramRequest.getName());
        fitnessProgramEntity.setDescription(fitnessProgramRequest.getDescription());
        fitnessProgramEntity.setDifficultyLevel(fitnessProgramRequest.getDifficultyLevel());
        fitnessProgramEntity.setDuration(fitnessProgramRequest.getDuration());
        fitnessProgramEntity.setPrice(fitnessProgramRequest.getPrice());
        fitnessProgramEntity.setYoutubeUrl(fitnessProgramRequest.getYoutubeUrl());

        if (fitnessProgramRequest.getCategoryId() != null) {
            CategoryEntity category = categoryRepository.findById(fitnessProgramRequest.getCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException("Kategorija nije pronađena"));
            fitnessProgramEntity.setCategory(category);
        }
        if (fitnessProgramRequest.getLocationId() != null) {
            LocationEntity location = locationRepository.findById(fitnessProgramRequest.getLocationId())
                    .orElseThrow(() -> new LocationNotFoundException("Lokacija nije pronađena"));
            fitnessProgramEntity.setLocation(location);
        } else {
            fitnessProgramEntity.setLocation(null);
        }

        if (removedImages != null && !removedImages.isEmpty()) {
            for (String imageUrl : removedImages) {
                ProgramImageEntity imageEntity = programImageRepository.findByImageUrl(imageUrl)
                        .orElseThrow(ImageUploadException::new);
                programImageRepository.delete(imageEntity);
                try {
                    this.imageUploadService.deleteImageFile(imageUrl);
                } catch (IOException e) {
                    System.err.println("Greška prilikom brisanja slike sa diska: " + imageUrl);
                    // TODO: Add something better here
                }
            }
        }

        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                String fileName = imageUploadService.uploadImage(file);
                String imageUrl = "/uploads/" + fileName;

                ProgramImageEntity programImage = new ProgramImageEntity();
                programImage.setFitnessProgram(fitnessProgramEntity);
                programImage.setImageUrl(imageUrl);
                programImageRepository.saveAndFlush(programImage);
            }
        }

        programAttributeRepository.deleteAll(fitnessProgramEntity.getProgramAttributes());
        if (fitnessProgramRequest.getSpecificAttributes() != null) {
            List<ProgramAttributeEntity> programAttributes = new ArrayList<>();
            for (FitnessProgramRequest.SpecificAttribute attribute : fitnessProgramRequest.getSpecificAttributes()) {
                ProgramAttributeEntity programAttributeEntity = new ProgramAttributeEntity();

                AttributeValueEntity attributeValueEntity = attributeValueRepository.findById(attribute.getAttributeValue())
                        .orElseThrow(() -> new AttributeValueNotFoundException("Vrijednost atributa nije pronađena"));

                programAttributeEntity.setFitnessProgram(fitnessProgramEntity);
                programAttributeEntity.setAttributeValue(attributeValueEntity);
                programAttributes.add(programAttributeEntity);
            }
            programAttributeRepository.saveAll(programAttributes);
            fitnessProgramEntity.setProgramAttributes(programAttributes);
        }

        fitnessProgramRepository.saveAndFlush(fitnessProgramEntity);

        return new FitnessProgramResponse(fitnessProgramEntity.getId());
    }

    private AttributeDTO getAttributeDTO(ProgramAttributeEntity programAttribute) {
        AttributeDTO attributeDTO = new AttributeDTO();
        attributeDTO.setId(
                programAttribute
                        .getAttributeValue()
                        .getAttribute()
                        .getId()
        );
        attributeDTO.setName(
                programAttribute
                        .getAttributeValue()
                        .getAttribute()
                        .getName()
        );
        attributeDTO.setDescription(
                programAttribute
                        .getAttributeValue()
                        .getAttribute()
                        .getDescription()
        );

        List<AttributeValueDTO> attributeValues =
                programAttribute
                        .getAttributeValue()
                        .getAttribute()
                        .getAttributeValues()
                        .stream()
                        .map(value -> new AttributeValueDTO(value.getId(), value.getName()))
                        .collect(Collectors.toList());
        attributeDTO.setValues(attributeValues);

        attributeDTO.setSelectedValue(
                new AttributeValueDTO(
                        programAttribute
                                .getAttributeValue()
                                .getId(),
                        programAttribute
                                .getAttributeValue()
                                .getName())
        );

        return attributeDTO;
    }


    private FitnessProgramListResponse getFitnessProgramListResponse(FitnessProgramEntity program) {
        FitnessProgramListResponse programResponse = new FitnessProgramListResponse();
        programResponse.setId(program.getId());
        programResponse.setName(program.getName());
        programResponse.setDescription(program.getDescription());
        programResponse.setPrice(program.getPrice());
        programResponse.setDuration(program.getDuration());
        programResponse.setDifficultyLevel(program.getDifficultyLevel());
        programResponse.setYoutubeUrl(program.getYoutubeUrl());

        if (program.getLocation() != null) {
            programResponse.setLocationName(program.getLocation().getName());
        }

        return programResponse;
    }

}
