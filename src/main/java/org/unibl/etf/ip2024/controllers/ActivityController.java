package org.unibl.etf.ip2024.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.unibl.etf.ip2024.models.dto.requests.ActivityRequest;
import org.unibl.etf.ip2024.models.dto.response.ActivityResponse;
import org.unibl.etf.ip2024.services.ActivityService;
import org.unibl.etf.ip2024.services.PdfService;

import java.io.ByteArrayInputStream;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/activities")
@RequiredArgsConstructor
public class ActivityController {
    private final ActivityService activityService;
    private final PdfService pdfService;

    @GetMapping
    public ResponseEntity<List<ActivityResponse>> getAllActivitiesByUser(Principal principal) {
        return ResponseEntity.ok(activityService.getAllActivitiesByUser(principal));
    }

    @PostMapping
    public ResponseEntity<ActivityResponse> addActivity(Principal principal, @RequestBody ActivityRequest activityRequest) {
        return ResponseEntity.ok(activityService.addActivity(principal, activityRequest));
    }

    @GetMapping("/download-pdf")
    public ResponseEntity<byte[]> downloadActivityReport(Principal principal) {
        List<ActivityResponse> activities = activityService.getAllActivitiesByUser(principal);
        ByteArrayInputStream pdfStream = pdfService.generateActivityReport(principal, activities);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=activity_report.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfStream.readAllBytes());
    }
}
