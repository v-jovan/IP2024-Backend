package org.unibl.etf.ip2024.services;

import org.springframework.stereotype.Service;
import org.unibl.etf.ip2024.models.dto.response.ActivityResponse;

import java.io.ByteArrayInputStream;
import java.security.Principal;
import java.util.List;

@Service
public interface PdfService {
    ByteArrayInputStream generateActivityReport(Principal principal, List<ActivityResponse> activities);
}
