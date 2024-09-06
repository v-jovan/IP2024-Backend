package org.unibl.etf.ip2024.services.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.unibl.etf.ip2024.exceptions.UserNotFoundException;
import org.unibl.etf.ip2024.models.dto.requests.ActivityRequest;
import org.unibl.etf.ip2024.models.dto.response.ActivityResponse;
import org.unibl.etf.ip2024.models.entities.ActivityEntity;
import org.unibl.etf.ip2024.models.entities.UserEntity;
import org.unibl.etf.ip2024.repositories.ActivityEntityRepository;
import org.unibl.etf.ip2024.repositories.UserEntityRepository;
import org.unibl.etf.ip2024.services.ActivityService;

import java.security.Principal;
import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityServiceImpl implements ActivityService {

    private final ActivityEntityRepository activityRepository;
    private final UserEntityRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<ActivityResponse> getAllActivitiesByUser(Principal principal) {
        UserEntity user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("Korisnik nije pronađen"));

        List<ActivityEntity> activityEntities = activityRepository.findAllByUser(user);

        return activityEntities
                .stream()
                .map(activityEntity -> modelMapper.map(activityEntity, ActivityResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public ActivityResponse addActivity(Principal principal, ActivityRequest activityRequest) {
        UserEntity user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("Korisnik nije pronađen"));

        ActivityEntity activity = new ActivityEntity();
        activity.setUser(user);
        activity.setActivityType(activityRequest.getActivityType());
        activity.setDuration(activityRequest.getDuration());
        activity.setIntensity(activityRequest.getIntensity());
        activity.setResult(activityRequest.getResult());
        activity.setLogDate(Date.valueOf(activityRequest.getLogDate()));

        ActivityEntity savedActivity = activityRepository.saveAndFlush(activity);

        return modelMapper.map(savedActivity, ActivityResponse.class);
    }
}
