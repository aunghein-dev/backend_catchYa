package com.catch_ya_group.catch_ya.modal.presence;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class PresenceEvent {
    private long userId;
    private boolean online;   // true = online, false = offline
    private long lastSeen;    // epoch millis
}
