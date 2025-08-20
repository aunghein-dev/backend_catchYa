package com.catch_ya_group.catch_ya.modal.dto;

import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class ChatHistory {
    private Long id;
    private Long senderId;
    private  Long recipientId;
    private  String content;
    private String status;
    private Instant createdAt;
    private Instant deliveredAt;
    private Instant readAt;
    private List<ChatReactionHistory> chatReactionHistoryList;
}
