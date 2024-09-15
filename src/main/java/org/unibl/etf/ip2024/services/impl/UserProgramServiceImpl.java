package org.unibl.etf.ip2024.services.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.unibl.etf.ip2024.exceptions.ProgramNotFoundException;
import org.unibl.etf.ip2024.exceptions.UserNotFoundException;
import org.unibl.etf.ip2024.models.dto.response.FitnessProgramListResponse;
import org.unibl.etf.ip2024.models.dto.response.UserProgramResponse;
import org.unibl.etf.ip2024.models.entities.FitnessProgramEntity;
import org.unibl.etf.ip2024.models.entities.UserEntity;
import org.unibl.etf.ip2024.models.entities.UserProgramEntity;
import org.unibl.etf.ip2024.models.enums.Status;
import org.unibl.etf.ip2024.repositories.FitnessProgramEntityRepository;
import org.unibl.etf.ip2024.repositories.UserEntityRepository;
import org.unibl.etf.ip2024.repositories.UserProgramEntityRepository;
import org.unibl.etf.ip2024.services.LogService;
import org.unibl.etf.ip2024.services.UserProgramService;

import java.security.Principal;
import java.time.LocalDate;

@Service
public class UserProgramServiceImpl implements UserProgramService {

    @Value("${fitness.program.duration}")
    private Integer programDuration;

    private final UserEntityRepository userRepository;
    private final UserProgramEntityRepository userProgramRepository;
    private final FitnessProgramEntityRepository fitnessProgramRepository;
    private final LogService logService;

    public UserProgramServiceImpl(UserEntityRepository userRepository, UserProgramEntityRepository userProgramRepository, FitnessProgramEntityRepository fitnessProgramRepository, LogService logService) {
        this.userRepository = userRepository;
        this.userProgramRepository = userProgramRepository;
        this.fitnessProgramRepository = fitnessProgramRepository;
        this.logService = logService;
    }

    @Override
    public Page<FitnessProgramListResponse> getUserPrograms(Principal principal, Pageable pageable) {
        UserEntity user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("Korisnik nije pronađen"));

        Page<UserProgramEntity> userProgramsPage = userProgramRepository.findAllByUserByUserId(user, pageable);

        LocalDate now = LocalDate.now();

        userProgramsPage.forEach(userProgram -> {
            if (now.isAfter(userProgram.getEndDate().toLocalDate())) {
                if (userProgram.getStatus() == Status.ACTIVE) {
                    userProgram.setStatus(Status.INACTIVE);
                    userProgramRepository.saveAndFlush(userProgram);
                }
            }
        });

        logService.log(principal, "Prikaz svih programa korisnika");

        return userProgramsPage.map(this::mapToFitnessProgramListResponse);
    }

    @Override
    public UserProgramResponse createUserProgram(Principal principal, Integer programId) {
        UserEntity user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("Korisnik nije pronađen"));
        FitnessProgramEntity fitnessProgram = fitnessProgramRepository.findById(programId)
                .orElseThrow(() -> new ProgramNotFoundException("Program nije pronađen"));

        UserProgramEntity userProgram = new UserProgramEntity();
        userProgram.setUserByUserId(user);
        userProgram.setFitnessProgramByProgramId(fitnessProgram);

        userProgram.setStartDate(new java.sql.Date(System.currentTimeMillis()));

        LocalDate endDate = LocalDate.now().plusDays(this.programDuration);
        userProgram.setEndDate(java.sql.Date.valueOf(endDate));
        userProgram.setStatus(Status.ACTIVE);

        UserProgramEntity savedUser = userProgramRepository.saveAndFlush(userProgram);

        logService.log(principal, "Kreiranje programa");

        return mapToUserProgramResponse(savedUser);
    }

    @Override
    public void deleteUserProgram(Principal principal, Integer programId) {
        UserEntity user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("Korisnik nije pronađen"));

        UserProgramEntity userProgram = userProgramRepository.findById(programId)
                .orElseThrow(() -> new ProgramNotFoundException("Program nije pronađen"));

        if (!userProgram.getUserByUserId().getId().equals(user.getId())) {
            throw new ProgramNotFoundException("Program nije pronađen");
        }

        logService.log(principal, "Brisanje programa");

        userProgramRepository.delete(userProgram);
    }


    private UserProgramResponse mapToUserProgramResponse(UserProgramEntity userProgram) {
        UserProgramResponse response = new UserProgramResponse();
        response.setId(userProgram.getId());
        response.setProgramName(userProgram.getFitnessProgramByProgramId().getName());
        response.setStartDate(userProgram.getStartDate());
        response.setEndDate(userProgram.getEndDate());
        response.setStatus(userProgram.getStatus().name());

        return response;
    }

    private FitnessProgramListResponse mapToFitnessProgramListResponse(UserProgramEntity userProgram) {
        FitnessProgramEntity program = userProgram.getFitnessProgramByProgramId();
        FitnessProgramListResponse response = new FitnessProgramListResponse();

        response.setId(program.getId());
        response.setName(program.getName());
        response.setDescription(program.getDescription());
        response.setDuration(program.getDuration());
        response.setPrice(program.getPrice());
        response.setDifficultyLevel(program.getDifficultyLevel());
        response.setYoutubeUrl(program.getYoutubeUrl());
        response.setPurchaseId(userProgram.getId());

        if (program.getLocation() != null) {
            response.setLocationName(program.getLocation().getName());
        }

        if (program.getUser() != null) {
            response.setInstructorName(this.generateInstructorName(program.getUser()));
            response.setInstructorId(program.getUser().getId());
        }

        response.setStartDate(userProgram.getStartDate());
        response.setEndDate(userProgram.getEndDate());
        response.setStatus(userProgram.getStatus().toString());

        return response;
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
