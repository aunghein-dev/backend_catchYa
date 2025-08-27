// src/main/java/com/catch_ya_group/catch_ya/controller/presence/PresenceController.java
package com.catch_ya_group.catch_ya.controller.presence;

import com.catch_ya_group.catch_ya.modal.presence.PresenceEvent;
import com.catch_ya_group.catch_ya.service.presence.PresenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Tag(
        name = "User Presence",
        description = "APIs and WebSocket endpoints for tracking user online/offline status and last-seen timestamps."
)
public class PresenceController {
    private final PresenceService presence;

    // STOMP: clients send to /app/presence.ping (no body needed)
    @MessageMapping("/presence.ping")
    public void wsPing(Map<String, Object> ignored, Principal principal) {
        long userId = Long.parseLong(principal.getName()); // adjust if your Principal differs
        presence.ping(userId);
    }

    @Operation(
            summary = "Get last-seen timestamps",
            description = "Returns the most recent presence information (last ping time, online/offline status) for a comma-separated list of user IDs."
    )
    @GetMapping("/private/presence/last-seen")
    public Map<Long, PresenceEvent> lastSeen(@RequestParam("ids") String idsCsv) {
        List<Long> ids = Arrays.stream(idsCsv.split(","))
                .filter(s -> !s.isBlank())
                .map(String::trim).map(Long::valueOf).collect(Collectors.toList());
        return presence.batch(ids);
    }

    @Operation(
            summary = "Get all online users",
            description = "Returns a list of all users who are currently online based on their most recent presence ping."
    )
    @GetMapping("/private/presence/online")
    public List<PresenceEvent> getAllOnline() {
        return presence.allOnline();
    }

}
