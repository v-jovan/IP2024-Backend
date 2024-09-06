package org.unibl.etf.ip2024.services;

import org.springframework.stereotype.Service;
import org.unibl.etf.ip2024.models.dto.requests.ActivityRequest;
import org.unibl.etf.ip2024.models.dto.response.ActivityResponse;

import java.security.Principal;
import java.util.List;

@Service
public interface ActivityService {
    List<ActivityResponse> getAllActivitiesByUser(Principal principal);
    ActivityResponse addActivity(Principal principal, ActivityRequest activityRequest);
}
