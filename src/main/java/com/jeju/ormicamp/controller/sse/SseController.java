package com.jeju.ormicamp.controller.sse;

import com.jeju.ormicamp.common.customUserDetail.UserPrincipal;
import com.jeju.ormicamp.infrastructure.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sse")
public class SseController {

    private final SseService sseService;

    @GetMapping("/disasters")
    //public SseEmitter connect(@AuthenticationPrincipal UserPrincipal userPrincipal) {
    public SseEmitter connect() {
        System.out.println("sse 연결");
        //Long userId = userPrincipal.userId();
        Long userId = 1L;
        return sseService.connect(userId);
    }
}
