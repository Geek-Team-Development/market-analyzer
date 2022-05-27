package ru.manalyzer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import ru.manalyzer.service.NotificationService;
import ru.manalyzer.service.dto.MessageToFront;

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
    public Flux<MessageToFront> getNotifyMessages(Principal principal) {
        return notificationService.getNotifyMessages(principal.getName());
    }

    @PostMapping("/{id}")
    public void markAsRead(@PathVariable String id, Principal principal) {
        notificationService.markAsRead(principal.getName(), id);
    }
}
