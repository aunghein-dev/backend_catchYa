package com.catch_ya_group.catch_ya.modal.chatpayload;
import lombok.Data;

@Data
public class ChatReact {
    private Long messageId;
    private String clientMessageId;  // optional fallback
    private String actorId;          // who reacted
    private String emoji;            // 👍❤️😂😮😢😡
}
