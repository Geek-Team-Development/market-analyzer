package ru.manalyzer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import ru.manalyzer.dto.NotifyMessageDto;
import ru.manalyzer.service.NotificationService;

import java.security.Principal;

@PreAuthorize("isAuthenticated()")
@Secured({"ROLE_USER", "ROLE_ADMIN"})
@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping(produces = "text/event-stream")
    public Flux<NotifyMessageDto> getNotifyMessages(Principal principal) {
        return notificationService.getNotifyMessages(principal.getName());
    }

    @PostMapping("/{id}")
    public void markAsRead(String notifyMessageId, Principal principal) {
        notificationService.markAsRead(principal.getName(), notifyMessageId);
    }
}
