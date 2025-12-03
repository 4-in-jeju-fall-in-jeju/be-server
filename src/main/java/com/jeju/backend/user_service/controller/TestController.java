package com.jeju.backend.user_service.controller;

import com.jeju.backend.user_service.model.UserProfile;
import com.jeju.backend.user_service.model.dto.request.UserProfileReqDto;
import com.jeju.backend.user_service.service.UserProfileService;
import com.sun.net.httpserver.Authenticator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final UserProfileService userProfileService;

    @GetMapping("/info")
    public ResponseEntity<Void> getInfo(UserProfileReqDto userProfileReqDto) {

        userProfileService.save(userProfileReqDto);

        return ResponseEntity.ok().build();
    }
}
