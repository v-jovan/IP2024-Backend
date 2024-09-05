package org.unibl.etf.ip2024.services.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.unibl.etf.ip2024.exceptions.ProgramNotFoundException;
import org.unibl.etf.ip2024.exceptions.UserNotFoundException;
import org.unibl.etf.ip2024.models.dto.response.UserProgramResponse;
import org.unibl.etf.ip2024.models.entities.FitnessProgramEntity;
import org.unibl.etf.ip2024.models.entities.UserEntity;
import org.unibl.etf.ip2024.models.entities.UserProgramEntity;
import org.unibl.etf.ip2024.models.enums.Status;
import org.unibl.etf.ip2024.repositories.FitnessProgramEntityRepository;
import org.unibl.etf.ip2024.repositories.UserEntityRepository;
import org.unibl.etf.ip2024.repositories.UserProgramEntityRepository;
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

    public UserProgramServiceImpl(UserEntityRepository userRepository, UserProgramEntityRepository userProgramRepository, FitnessProgramEntityRepository fitnessProgramRepository) {
        this.userRepository = userRepository;
        this.userProgramRepository = userProgramRepository;
        this.fitnessProgramRepository = fitnessProgramRepository;
    }

    @Override
    public Page<UserProgramResponse> getUserPrograms(Principal principal, Pageable pageable) {
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

        return userProgramsPage.map(this::mapToUserProgramResponse);
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

        return mapToUserProgramResponse(savedUser);
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

}
