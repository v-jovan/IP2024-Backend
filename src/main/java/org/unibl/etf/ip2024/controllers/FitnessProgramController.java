package org.unibl.etf.ip2024.controllers;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.unibl.etf.ip2024.models.dto.requests.FitnessProgramRequest;
import org.unibl.etf.ip2024.models.dto.response.FitnessProgramResponse;
import org.unibl.etf.ip2024.services.FitnessProgramService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/programs")
@RequiredArgsConstructor
public class FitnessProgramController {
    private final FitnessProgramService fitnessProgramService;

    Logger log = LoggerFactory.getLogger(FitnessProgramController.class);

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<FitnessProgramResponse> createProgram(
            @RequestPart("program") FitnessProgramRequest programRequest,
            @RequestPart("files") List<MultipartFile> files,
            Principal principal) {
        log.info("Received request with program: {}", programRequest);
        log.info("Received files: {}", files.size());
        files.forEach(file -> log.info("File name: {}, size: {}", file.getOriginalFilename(), file.getSize()));
        try {
            FitnessProgramResponse response = fitnessProgramService.addFitnessProgram(principal, programRequest, files);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error while creating program", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
