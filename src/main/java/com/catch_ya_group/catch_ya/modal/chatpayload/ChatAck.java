package com.catch_ya_group.catch_ya.modal.chatpayload;
import lombok.Data;

@Data
public class ChatAck {
    private Long messageId;          // prefer server id
    private String clientMessageId;  // fallback if needed
    private String type;             // "DELIVERED" or "READ"
    private String actorId;          // who is acknowledging (recipient)
}