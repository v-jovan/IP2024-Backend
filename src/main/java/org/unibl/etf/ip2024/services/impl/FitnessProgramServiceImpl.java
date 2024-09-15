package org.unibl.etf.ip2024.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.unibl.etf.ip2024.exceptions.*;
import org.unibl.etf.ip2024.models.dto.AttributeDTO;
import org.unibl.etf.ip2024.models.dto.AttributeValueDTO;
import org.unibl.etf.ip2024.models.dto.CategoryDTO;
import org.unibl.etf.ip2024.models.dto.requests.FitnessProgramRequest;
import org.unibl.etf.ip2024.models.dto.response.FitnessProgramHomeResponse;
import org.unibl.etf.ip2024.models.dto.response.FitnessProgramListResponse;
import org.unibl.etf.ip2024.models.dto.response.FitnessProgramResponse;
import org.unibl.etf.ip2024.models.entities.*;
import org.unibl.etf.ip2024.repositories.*;
import org.unibl.etf.ip2024.services.FitnessProgramService;
import org.unibl.etf.ip2024.services.ImageUploadService;
import org.unibl.etf.ip2024.services.LogService;

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
    private final LogService logService;
    private final ModelMapper modelMapper;

    /**
     * Adds a new fitness program based on the provided request and files.
     *
     * @param principal             the security principal of the authenticated user
     * @param fitnessProgramRequest the request object containing the details of the fitness program to be added
     * @param files                 the list of files to be associated with the fitness program, can be null
     * @return a FitnessProgramResponse object containing the ID of the newly created fitness program
     * @throws IOException                     if an I/O error occurs during file upload
     * @throws ProgramAlreadyExistsException   if a fitness program with the same name already exists
     * @throws IllegalArgumentException        if the program is neither online nor offline, or both
     * @throws CategoryNotFoundException       if the specified category is not found
     * @throws UserNotFoundException           if the authenticated user is not found
     * @throws LocationNotFoundException       if the specified location is not found
     * @throws AttributeValueNotFoundException if a specified attribute value is not found
     */
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

        logService.log(principal, "Dodavanje fitness programa");

        return new FitnessProgramResponse(savedProgram.getId());
    }

    /**
     * Retrieves the fitness programs created by the authenticated user.
     *
     * @param principal the security principal of the authenticated user
     * @param pageable  the pagination information
     * @return a Page of FitnessProgramListResponse objects
     * @throws UserNotFoundException if the authenticated user is not found
     */
    @Override
    @Transactional
    public Page<FitnessProgramListResponse> getMyFitnessPrograms(Principal principal, Pageable pageable) {
        Page<FitnessProgramEntity> programs;
        UserEntity user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("Korisnik nije pronađen"));
        programs = fitnessProgramRepository.findAllByUserId(user.getId(), pageable);

        logService.log(principal, "Pregled mojih fitness programa");

        return programs.map(this::getFitnessProgramListResponse);
    }

    /**
     * Retrieves all fitness programs with pagination.
     *
     * @param pageable the pagination information
     * @return a Page of FitnessProgramHomeResponse objects
     */
    @Override
    @Transactional
    public Page<FitnessProgramHomeResponse> getAllFitnessPrograms(Pageable pageable) {
        Page<FitnessProgramEntity> programs = fitnessProgramRepository.findAll(pageable);
        logService.log(null, "Pregled svih fitness programa");
        return programs.map(this::getFitnessProgramHomeResponse);
    }

    /**
     * Retrieves a specific fitness program by its ID.
     *
     * @param id the ID of the fitness program to retrieve
     * @return a FitnessProgramResponse object containing the details of the fitness program
     * @throws ProgramNotFoundException if the fitness program with the specified ID is not found
     */
    @Override
    public FitnessProgramResponse getFitnessProgram(Integer id) {
        FitnessProgramEntity programEntity = fitnessProgramRepository.findById(id)
                .orElseThrow(() -> new ProgramNotFoundException("Program sa ID-jem " + id + " nije pronađen."));

        // Create a new FitnessProgramResponse
        FitnessProgramResponse response = new FitnessProgramResponse();

        // Set the fields in the response
        response.setId(programEntity.getId());
        response.setName(programEntity.getName());
        response.setDescription(programEntity.getDescription());
        response.setDuration(programEntity.getDuration());
        response.setPrice(programEntity.getPrice());
        response.setDifficultyLevel(programEntity.getDifficultyLevel());
        response.setYoutubeUrl(programEntity.getYoutubeUrl());
        response.setInstructorName(this.generateInstructorName(programEntity.getUser()));
        response.setInstructorId(programEntity.getUser().getId());

        // Map the location
        if (programEntity.getLocation() != null) {
            response.setLocationId(programEntity.getLocation().getId());
            response.setLocationName(programEntity.getLocation().getName());
        }

        // Map the category
        if (programEntity.getCategory() != null) {
            response.setCategoryId(programEntity.getCategory().getId());
            response.setCategoryName(programEntity.getCategory().getName());
        }

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

        logService.log(null, "Pregled fitness programa sa ID-jem " + id);

        return response;
    }

    /**
     * Updates an existing fitness program based on the provided request and files.
     *
     * @param programId             the ID of the fitness program to update
     * @param fitnessProgramRequest the request object containing the updated details of the fitness program
     * @param files                 the list of new files to be associated with the fitness program, can be null
     * @param removedImages         the list of image filenames to be removed from the fitness program, can be null
     * @return a FitnessProgramResponse object containing the ID of the updated fitness program
     * @throws IOException                     if an I/O error occurs during file upload or deletion
     * @throws ProgramNotFoundException        if the fitness program with the specified ID is not found
     * @throws CategoryNotFoundException       if the specified category is not found
     * @throws LocationNotFoundException       if the specified location is not found
     * @throws AttributeValueNotFoundException if a specified attribute value is not found
     * @throws ImageUploadException            if an error occurs during image upload
     */
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
                } catch (IOException ignored) {
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

        logService.log(null, "Ažuriranje fitness programa sa ID-jem " + programId);

        fitnessProgramRepository.saveAndFlush(fitnessProgramEntity);

        return new FitnessProgramResponse(fitnessProgramEntity.getId());
    }

    /**
     * Retrieves all fitness programs filtered by a specific attribute value with pagination.
     *
     * @param attributeValueId the ID of the attribute value to filter by
     * @param pageable         the pagination information
     * @return a Page of FitnessProgramHomeResponse objects
     */
    @Override
    public Page<FitnessProgramHomeResponse> getAllFitnessProgramsByAttributeValue(Integer attributeValueId, Pageable pageable) {
        Page<FitnessProgramEntity> programs = fitnessProgramRepository.findDistinctByProgramAttributes_AttributeValue_Id(attributeValueId, pageable);
        logService.log(null, "Pregled fitness programa sa vrijednost atributa sa ID-jem " + attributeValueId);
        return programs.map(this::getFitnessProgramHomeResponse);
    }

    /**
     * Retrieves all fitness programs filtered by a specific attribute ID with pagination.
     *
     * @param attributeId the ID of the attribute to filter by
     * @param pageable    the pagination information
     * @return a Page of FitnessProgramHomeResponse objects
     */
    @Override
    public Page<FitnessProgramHomeResponse> getAllFitnessProgramsByAttributeId(Integer attributeId, Pageable pageable) {
        List<AttributeValueEntity> attributeValues = attributeValueRepository.findByAttributeId(attributeId);
        List<Integer> attributeValueIds = attributeValues
                .stream()
                .map(AttributeValueEntity::getId)
                .collect(Collectors.toList());
        Page<FitnessProgramEntity> programs = fitnessProgramRepository.findDistinctByProgramAttributes_AttributeValue_IdIn(attributeValueIds, pageable);
        logService.log(null, "Pregled fitness programa sa atributom sa ID-jem " + attributeId);
        return programs.map(this::getFitnessProgramHomeResponse);
    }

    /**
     * Retrieves all fitness programs filtered by a specific category ID with pagination.
     *
     * @param categoryId the ID of the category to filter by
     * @param pageable   the pagination information
     * @return a Page of FitnessProgramHomeResponse objects
     */
    @Override
    public Page<FitnessProgramHomeResponse> getAllFitnessProgramsByCategoryId(Integer categoryId, Pageable pageable) {
        Page<FitnessProgramEntity> programs = fitnessProgramRepository.findAllByCategoryId(categoryId, pageable);
        logService.log(null, "Pregled fitness programa sa kategorijom sa ID-jem " + categoryId);
        return programs.map(this::getFitnessProgramHomeResponse);
    }

    /**
     * Retrieves all categories with their associated attributes and attribute values.
     * Only categories with non-empty attributes are included in the result.
     *
     * @return a list of CategoryDTO objects with their attributes and attribute values
     */
    @Override
    public List<CategoryDTO> getAllCategoriesWithAttributesAndValues() {
        List<CategoryEntity> categories = categoryRepository.findAllWithProgramsAndAttributesAndValues();
        logService.log(null, "Pregled svih kategorija sa atributima i vrijednostima");

        return categories.stream()
                .map(this::convertToCategoryDTO)
                .filter(categoryDTO -> !categoryDTO.getAttributes().isEmpty())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteFitnessProgram(Integer programId, Principal principal) throws IOException {
        FitnessProgramEntity program = fitnessProgramRepository.findById(programId)
                .orElseThrow(() -> new ProgramNotFoundException("Program sa ID-jem " + programId + " nije pronađen."));

        UserEntity currentUser = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("Korisnik nije pronađen"));

        if (!program.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedAccessException("Nemate dozvolu da obrišete ovaj program.");
        }

        List<ProgramImageEntity> programImages = program.getProgramImages();

        for (ProgramImageEntity imageEntity : programImages) {
            imageUploadService.deleteImageFile(imageEntity.getImageUrl());
        }

        logService.log(principal, "Brisanje fitness programa sa ID-jem " + programId);

        fitnessProgramRepository.delete(program);
    }


    /**
     * Converts a CategoryEntity to a CategoryDTO.
     *
     * @param categoryEntity the CategoryEntity to convert
     * @return the converted CategoryDTO
     */
    private CategoryDTO convertToCategoryDTO(CategoryEntity categoryEntity) {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(categoryEntity.getId());
        categoryDTO.setName(categoryEntity.getName());
        categoryDTO.setDescription(categoryEntity.getDescription());

        List<AttributeDTO> attributes = categoryEntity.getAttributes().stream()
                .map(this::convertToAttributeDTO)
                .filter(attributeDTO -> !attributeDTO.getValues().isEmpty())
                .collect(Collectors.toList());

        categoryDTO.setAttributes(attributes);

        return categoryDTO;
    }

    /**
     * Converts an AttributeEntity to an AttributeDTO.
     *
     * @param attributeEntity the AttributeEntity to convert
     * @return the converted AttributeDTO
     */
    private AttributeDTO convertToAttributeDTO(AttributeEntity attributeEntity) {
        AttributeDTO attributeDTO = modelMapper.map(attributeEntity, AttributeDTO.class);

        attributeDTO.setValues(
                attributeEntity.getAttributeValues().stream()
                        .filter(value -> !value.getProgramAttributes().isEmpty())
                        .map(value -> new AttributeValueDTO(value.getId(), value.getName()))
                        .collect(Collectors.toList())
        );

        return attributeDTO;
    }

    /**
     * Converts a ProgramAttributeEntity to an AttributeDTO.
     *
     * @param programAttribute the ProgramAttributeEntity to convert
     * @return the converted AttributeDTO
     */
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

    /**
     * Converts a FitnessProgramEntity to a FitnessProgramListResponse.
     *
     * @param program the FitnessProgramEntity to convert
     * @return the converted FitnessProgramListResponse
     */
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

    /**
     * Converts a FitnessProgramEntity to a FitnessProgramHomeResponse.
     *
     * @param program the FitnessProgramEntity to convert
     * @return the converted FitnessProgramHomeResponse
     */
    private FitnessProgramHomeResponse getFitnessProgramHomeResponse(FitnessProgramEntity program) {
        FitnessProgramHomeResponse programResponse = new FitnessProgramHomeResponse();
        programResponse.setId(program.getId());
        programResponse.setName(program.getName());
        programResponse.setPrice(program.getPrice());
        programResponse.setInstructorId(program.getUser().getId());

        List<String> imageUrls = program
                .getProgramImages()
                .stream()
                .map(ProgramImageEntity::getImageUrl)
                .collect(Collectors.toList());
        programResponse.setImages(imageUrls);

        return programResponse;
    }

    private String generateInstructorName(UserEntity user) {
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        String username = user.getUsername();

        String instructorName;

        if (firstName != null && !firstName.isEmpty() && lastName != null && !lastName.isEmpty()) {
            instructorName = firstName + " " + lastName;
        } else if (firstName != null && !firstName.isEmpty()) {
            instructorName = firstName;
        } else if (lastName != null && !lastName.isEmpty()) {
            instructorName = lastName;
        } else {
            instructorName = username;
        }

        return instructorName;
    }
}