package com.catch_ya_group.catch_ya.service.chat;

import com.catch_ya_group.catch_ya.modal.chatpayload.*;
import com.catch_ya_group.catch_ya.modal.dto.ChatHistory;
import com.catch_ya_group.catch_ya.modal.dto.ChatReactionHistory;
import com.catch_ya_group.catch_ya.modal.entity.*;
import com.catch_ya_group.catch_ya.modal.projection.MessageStatus;
import com.catch_ya_group.catch_ya.repository.ChatMessageRepository;
import com.catch_ya_group.catch_ya.repository.MessageReactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
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

        ent = chatMessageRepository.save(ent);

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
                ? chatMessageRepository.findById(ack.getMessageId()).orElseThrow()
                : chatMessageRepository.findByClientMessageId(ack.getClientMessageId()).orElseThrow();

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
        ent = chatMessageRepository.save(ent);
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
                ? chatMessageRepository.findById(react.getMessageId()).orElseThrow()
                : chatMessageRepository.findByClientMessageId(react.getClientMessageId()).orElseThrow();

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

    public ChatListResponse getRecentChatListForUser(Long userId, int rows) {
        List<RecentMessageRow> raw = chatMessageRepository.getRecentChatListRows(userId, rows);

        ChatListResponse dto = new ChatListResponse();
        if (!raw.isEmpty()) {
            dto.setMineProImgUrl(raw.get(0).getMine_pro_img_url());
        }

        List<RecentMessageResponse> items = raw.stream().map(r -> {
            RecentMessageResponse m = new RecentMessageResponse();
            m.setOtherUserId(r.getOther_user_id());
            m.setOtherProImgUrl(r.getOther_pro_img_url());
            m.setOtherFullName(r.getOther_full_name());
            m.setOtherUniqueName(r.getOther_unique_name());
            m.setOtherPhoneNo(r.getOther_phone_no());
            m.setChatContent(r.getChat_content());
            m.setCreatedAt(r.getCreated_at());
            m.setSenderId(r.getSender_id());
            m.setRecipientId(r.getRecipient_id());
            return m;
        }).toList();

        dto.setRecentMessageResponse(items);
        return dto;
    }

    public List<ChatHistory> getChatHistory(Long currentUserId, Long targetUserId) {
        List<ChatMessageEntity> messages = chatMessageRepository.getChatThread(currentUserId, targetUserId);
        List<MessageReaction> reactions = chatMessageRepository.getChatReactions(currentUserId, targetUserId);

        Map<Long, List<ChatReactionHistory>> reactionsByMessage =
                reactions.stream().collect(Collectors.groupingBy(
                        r -> r.getMessage().getId(),   // <-- use associated message id
                        Collectors.mapping(r -> (ChatReactionHistory) new ChatReactionHistory() {
                            @Override public Long getId() { return r.getId(); }
                            @Override public Instant getCreatedAt() { return r.getCreatedAt(); }
                            @Override public String getEmoji() { return r.getEmoji(); }
                            @Override public Long getUserId() { return r.getUserId(); }
                            @Override public Long getMessageId() { return r.getMessage().getId(); }  // <-- here
                        }, Collectors.toList())
                ));

        return messages.stream().map(m -> {
            ChatHistory dto = new ChatHistory();
            dto.setId(m.getId());
            dto.setSenderId(m.getSenderId());
            dto.setRecipientId(m.getRecipientId());
            dto.setContent(m.getContent());
            dto.setStatus(m.getStatus().toString());
            dto.setCreatedAt(m.getCreatedAt());
            dto.setDeliveredAt(m.getDeliveredAt());
            dto.setReadAt(m.getReadAt());
            dto.setChatReactionHistoryList(reactionsByMessage.getOrDefault(m.getId(), List.of()));
            return dto;
        }).toList();
    }


}
