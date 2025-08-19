package com.catch_ya_group.catch_ya.service.chat;

import com.catch_ya_group.catch_ya.modal.chatpayload.ChatMessage;
import com.catch_ya_group.catch_ya.modal.entity.*;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class ChatMapper {
    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_INSTANT;

    public static ChatMessage toDto(ChatMessageEntity e, Map<String,Integer> reactions) {
        return ChatMessage.builder()
                .id(e.getId())
                .clientMessageId(e.getClientMessageId())
                .senderId(String.valueOf(e.getSenderId()))
                .recipientId(String.valueOf(e.getRecipientId()))
                .content(e.getContent())
                .timestamp(ISO.format(e.getCreatedAt().atOffset(ZoneOffset.UTC)))
                .status(e.getStatus().name())
                .deliveredAt(e.getDeliveredAt()==null?null:ISO.format(e.getDeliveredAt().atOffset(ZoneOffset.UTC)))
                .readAt(e.getReadAt()==null?null:ISO.format(e.getReadAt().atOffset(ZoneOffset.UTC)))
                .reactions(reactions)
                .build();
    }
}