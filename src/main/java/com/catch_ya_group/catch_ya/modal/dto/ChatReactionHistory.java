package com.catch_ya_group.catch_ya.modal.dto;

import lombok.Data;

import java.time.Instant;

public interface ChatReactionHistory {
    Long getId();
    Instant getCreatedAt();
    String getEmoji();
    Long getUserId();
    Long getMessageId();
}
