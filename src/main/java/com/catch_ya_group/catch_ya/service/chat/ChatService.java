package com.catch_ya_group.catch_ya.service.chat;

import com.catch_ya_group.catch_ya.modal.chatpayload.ChatAck;
import com.catch_ya_group.catch_ya.modal.chatpayload.ChatMessage;
import com.catch_ya_group.catch_ya.modal.chatpayload.ChatReact;
import com.catch_ya_group.catch_ya.modal.entity.*;
import com.catch_ya_group.catch_ya.modal.projection.MessageStatus;
import com.catch_ya_group.catch_ya.repository.ChatMessageRepository;
import com.catch_ya_group.catch_ya.repository.MessageReactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository messages;
    private final MessageReactionRepository reactions;
    private final SimpMessagingTemplate broker;

    // helper
    private Map<String,Integer> summarize(Long messageId) {
        return reactions.findByMessage_Id(messageId).stream()
                .collect(Collectors.groupingBy(MessageReaction::getEmoji, Collectors.summingInt(r -> 1)));
    }

    @Transactional
    public ChatMessage saveAndBroadcast(ChatMessage incoming) {
        ChatMessageEntity ent = ChatMessageEntity.builder()
                .clientMessageId(incoming.getClientMessageId())
                .senderId(Long.valueOf(incoming.getSenderId()))
                .recipientId(Long.valueOf(incoming.getRecipientId()))
                .content(incoming.getContent())
                .status(MessageStatus.SENT)
                .build();

        ent = messages.save(ent);

        ChatMessage payload = ChatMapper.toDto(ent, Map.of());
        // send to recipient and echo to sender
        String toRecipient = "/user/" + payload.getRecipientId() + "/queue/chat";
        String toSender    = "/user/" + payload.getSenderId()    + "/queue/chat";
        broker.convertAndSend(toRecipient, payload);
        broker.convertAndSend(toSender, payload);
        return payload;
    }

    @Transactional
    public ChatMessage handleAck(ChatAck ack) {
        ChatMessageEntity ent = (ack.getMessageId()!=null)
                ? messages.findById(ack.getMessageId()).orElseThrow()
                : messages.findByClientMessageId(ack.getClientMessageId()).orElseThrow();

        Instant now = Instant.now();
        MessageStatus current = ent.getStatus();
        if ("DELIVERED".equalsIgnoreCase(ack.getType())) {
            // don't downgrade from READ back to DELIVERED
            if (current == null || current.ordinal() < MessageStatus.DELIVERED.ordinal()) {
                if (ent.getDeliveredAt() == null) ent.setDeliveredAt(now);
                ent.setStatus(MessageStatus.DELIVERED);
            }
        } else if ("READ".equalsIgnoreCase(ack.getType())) {
            // always upgrade to READ
            ent.setReadAt(now);
            ent.setStatus(MessageStatus.READ);
            if (ent.getDeliveredAt() == null) ent.setDeliveredAt(now);
        }
        ent = messages.save(ent);
        ChatMessage statusUpdate = ChatMapper.toDto(ent, summarize(ent.getId()));

        // notify both sides on dedicated status topic
        String a = "/user/" + ent.getSenderId()    + "/queue/chat.status";
        String b = "/user/" + ent.getRecipientId() + "/queue/chat.status";
        broker.convertAndSend(a, statusUpdate);
        broker.convertAndSend(b, statusUpdate);
        return statusUpdate;
    }

    @Transactional
    public ChatMessage handleReaction(ChatReact react) {
        ChatMessageEntity ent = (react.getMessageId()!=null)
                ? messages.findById(react.getMessageId()).orElseThrow()
                : messages.findByClientMessageId(react.getClientMessageId()).orElseThrow();

        Long uid = Long.valueOf(react.getActorId());

        // upsert one reaction per user per message
        var existing = reactions.findByMessage_IdAndUserId(ent.getId(), uid);
        MessageReaction r = existing.orElse(
                MessageReaction.builder().message(ent).userId(uid).emoji(react.getEmoji()).build()
        );
        r.setEmoji(react.getEmoji());
        reactions.save(r);

        ChatMessage withReactions = ChatMapper.toDto(ent, summarize(ent.getId()));

        // notify both users on reaction topic
        String a = "/user/" + ent.getSenderId()    + "/queue/chat.react";
        String b = "/user/" + ent.getRecipientId() + "/queue/chat.react";
        broker.convertAndSend(a, withReactions);
        broker.convertAndSend(b, withReactions);
        return withReactions;
    }
}
