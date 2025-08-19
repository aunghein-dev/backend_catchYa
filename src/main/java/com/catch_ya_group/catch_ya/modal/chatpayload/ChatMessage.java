package com.catch_ya_group.catch_ya.modal.chatpayload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ChatMessage {
    private Long id;                 // server id
    private String clientMessageId;  // uuid from client
    private String senderId;
    private String recipientId;
    private String content;
    private String timestamp;        // ISO (createdAt)
    private String status;           // SENT/DELIVERED/READ
    private String deliveredAt;      // ISO or null
    private String readAt;           // ISO or null
    private Map<String, Integer> reactions; // emoji -> count
}