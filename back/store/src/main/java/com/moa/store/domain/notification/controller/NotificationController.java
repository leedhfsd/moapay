package com.moa.store.domain.notification.controller;

import com.moa.store.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

@CrossOrigin("http://localhost:5173")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/store/notification")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping(value = "/subscribe/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable UUID id) {
        // 클라이언트에서 결제 요청을 보내기 전, 구독을 해놓는다
        log.info("Subscribing to notification with id {}", id);
        return notificationService.subscribe(id);
    }
}
