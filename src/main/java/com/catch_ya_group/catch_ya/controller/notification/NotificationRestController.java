package com.catch_ya_group.catch_ya.controller.notification;

import com.catch_ya_group.catch_ya.service.notification.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notifications marked as read successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid user ID")
    })
    @PostMapping("/read")
        public ResponseEntity<?> readNotifications(@RequestParam Long userId){
        return ResponseEntity.ok(notificationService.readNotifications(userId));
    }
}
