package org.unibl.etf.ip2024.controllers;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.unibl.etf.ip2024.models.dto.requests.FitnessProgramRequest;
import org.unibl.etf.ip2024.models.dto.response.FitnessProgramListResponse;
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

    @GetMapping()
    public ResponseEntity<Page<FitnessProgramListResponse>> getPrograms(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            @RequestParam(value = "sort", required = false) String sort) {
        Pageable pageable = createPageable(page, size, sort);
        Page<FitnessProgramListResponse> programs = fitnessProgramService.getFitnessPrograms(null, pageable);
        return ResponseEntity.ok(programs);
    }

    @GetMapping("/my-programs")
    public ResponseEntity<Page<FitnessProgramListResponse>> getMyPrograms(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            @RequestParam(value = "sort", required = false) String sort,
            Principal principal) {
        Pageable pageable = createPageable(page, size, sort);
        Page<FitnessProgramListResponse> programs = fitnessProgramService.getFitnessPrograms(principal, pageable);
        return ResponseEntity.ok(programs);
    }

    private Pageable createPageable(int page, int size, String sort) {
        if (sort != null && !sort.isEmpty()) {
            String[] sortParams = sort.split(",");
            if (sortParams.length == 2) {
                String sortField = sortParams[0];
                Sort.Direction direction = Sort.Direction.fromString(sortParams[1]);
                return PageRequest.of(page, size, Sort.by(direction, sortField));
            } else {
                return PageRequest.of(page, size);
            }
        } else {
            return PageRequest.of(page, size, Sort.unsorted());
        }
    }
}
