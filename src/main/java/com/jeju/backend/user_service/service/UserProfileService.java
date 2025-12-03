package com.jeju.backend.user_service.service;

import com.jeju.backend.user_service.model.UserProfile;
import com.jeju.backend.user_service.model.dto.request.UserProfileReqDto;
import com.jeju.backend.user_service.repository.UserProfileRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    public void save(UserProfileReqDto userProfileReqDto) {

        UserProfile userDatat = UserProfile.builder()
                .id(userProfileReqDto.getId())
                .build();

        userProfileRepository.save(userDatat);
    }
}
