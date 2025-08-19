package com.catch_ya_group.catch_ya.controller.notification;

import com.catch_ya_group.catch_ya.service.notification.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/private/v1/notification")
@RequiredArgsConstructor
@Tag(name = "Notification", description = "Endpoints for reading notifications")
public class NotificationRestController {

    private final NotificationService notificationService;

    @Operation(
            summary = "Mark all notifications as read",
            description = "Marks all notifications of a specific user as read and returns the updated list."
    )
    @PostMapping("/read")
    public ResponseEntity<?> readNotifications(@RequestParam Long userId){
        return ResponseEntity.ok(notificationService.readNotifications(userId));
    }

    @Operation(
            summary = "Get all notifications for a user",
            description = "Get all notifications for a specific user as both read and unread notifications lists."
    )
    @GetMapping("/{userId}")
    public ResponseEntity<?> getNotificationForUser(@PathVariable Long userId){
        return ResponseEntity.ok(notificationService.getNotificationForUser(userId));
    }

}
