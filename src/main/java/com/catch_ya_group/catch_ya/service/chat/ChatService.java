    // src/main/java/com/catch_ya_group/catch_ya/service/chat/ChatService.java
    package com.catch_ya_group.catch_ya.service.chat;

    import com.catch_ya_group.catch_ya.modal.chatpayload.*;
    import com.catch_ya_group.catch_ya.modal.dto.ChatHistory;
    import com.catch_ya_group.catch_ya.modal.dto.ChatReactionHistory;
    import com.catch_ya_group.catch_ya.modal.entity.ChatMessageEntity;
    import com.catch_ya_group.catch_ya.modal.entity.MessageReaction;
    import com.catch_ya_group.catch_ya.modal.projection.MessageStatus;
    import com.catch_ya_group.catch_ya.repository.ChatMessageRepository;
    import com.catch_ya_group.catch_ya.repository.MessageReactionRepository;
    import lombok.RequiredArgsConstructor;
    import org.springframework.data.domain.PageRequest;
    import org.springframework.data.domain.Pageable;
    import org.springframework.messaging.simp.SimpMessagingTemplate;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;

    import java.time.Instant;
    import java.util.*;
    import java.util.stream.Collectors;

    @Service
    @RequiredArgsConstructor
    public class ChatService {

        private final ChatMessageRepository chatMessageRepository;
        private final MessageReactionRepository reactions;
        private final SimpMessagingTemplate broker;
        private final ChatCacheService cache;

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

            // cache hot tail + list rows + unread
            cache.cacheNewMessage(ent);

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
                if (current == null || current.ordinal() < MessageStatus.DELIVERED.ordinal()) {
                    if (ent.getDeliveredAt() == null) ent.setDeliveredAt(now);
                    ent.setStatus(MessageStatus.DELIVERED);
                }
            } else if ("READ".equalsIgnoreCase(ack.getType())) {
                ent.setReadAt(now);
                ent.setStatus(MessageStatus.READ);
                if (ent.getDeliveredAt() == null) ent.setDeliveredAt(now);

                // mark read in cache (assume recipient is the reader)
                cache.markRead(ent.getRecipientId(), ent.getSenderId(), now);
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

        // =========================
        // Recent list (Redis → DB fallback)
        // =========================
        public ChatListResponse getRecentChatListForUser(Long userId, int rows) {
            // 1) Try Redis
            var cacheRows = cache.chatListFromCache(userId, rows);
            if (!cacheRows.isEmpty()) {
                ChatListResponse dto = new ChatListResponse();
                List<RecentMessageResponse> items = new ArrayList<>(cacheRows.size());
                for (var h : cacheRows) {
                    RecentMessageResponse m = new RecentMessageResponse();
                    m.setOtherUserId(Long.valueOf(h.getOrDefault("otherUserId", "0")));
                    m.setChatContent(h.getOrDefault("lastText", ""));
                    String at = h.get("lastMessageAt");
                    if (at != null) m.setCreatedAt(Date.from(Instant.ofEpochMilli(Long.parseLong(at))));
                    String sid = h.get("lastSenderId");
                    if (sid != null) m.setSenderId(Long.parseLong(sid));
                    m.setRecipientId(userId);
                    // optional: set profile fields if you cached them in conv hash
                    items.add(m);
                }
                dto.setRecentMessageResponse(items);
                return dto;
            }

            // 2) Fallback to DB (your existing logic)
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

        // =========================
        // History (existing full load)
        // =========================
        public List<ChatHistory> getChatHistory(Long currentUserId, Long targetUserId) {
            List<ChatMessageEntity> messages = chatMessageRepository.getChatThread(currentUserId, targetUserId);
            List<MessageReaction> reactions = chatMessageRepository.getChatReactions(currentUserId, targetUserId);

            Map<Long, List<ChatReactionHistory>> reactionsByMessage =
                    reactions.stream().collect(Collectors.groupingBy(
                            r -> r.getMessage().getId(),
                            Collectors.mapping(r -> (ChatReactionHistory) new ChatReactionHistory() {
                                @Override public Long getId() { return r.getId(); }
                                @Override public Instant getCreatedAt() { return r.getCreatedAt(); }
                                @Override public String getEmoji() { return r.getEmoji(); }
                                @Override public Long getUserId() { return r.getUserId(); }
                                @Override public Long getMessageId() { return r.getMessage().getId(); }
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

        public List<ChatHistory> getChatHistoryPaged(Long currentUserId, Long targetUserId, Long beforeTs, int limit) {
            // 1) Redis hot-tail
            var raw = cache.hotHistoryRaw(currentUserId, targetUserId, beforeTs, limit);
            List<ChatHistory> fromCache = raw.stream().map(h -> {
                ChatHistory dto = new ChatHistory();
                dto.setId(Long.parseLong(h.get("id")));
                dto.setSenderId(Long.parseLong(h.get("senderId")));
                dto.setRecipientId(Long.parseLong(h.get("recipientId")));
                dto.setContent(h.getOrDefault("content", ""));
                dto.setStatus(h.getOrDefault("status", MessageStatus.SENT.name()));
                String ca = h.get("createdAt");
                if (ca != null) dto.setCreatedAt(Instant.ofEpochMilli(Long.parseLong(ca)));
                String da = h.get("deliveredAt");
                if (da != null) dto.setDeliveredAt(Instant.ofEpochMilli(Long.parseLong(da)));
                String ra = h.get("readAt");
                if (ra != null) dto.setReadAt(Instant.ofEpochMilli(Long.parseLong(ra)));
                dto.setChatReactionHistoryList(List.of());
                return dto;
            }).toList();

            if (fromCache.size() >= limit) return fromCache; // already newest→oldest

            // 2) DB fallback (avoid overlap)
            int remaining = limit - fromCache.size();
            Pageable page = PageRequest.of(0, remaining);

            // cutoff: if caller gave beforeTs use it; else use oldest timestamp from cache page
            Long cutoffTs = beforeTs;
            if (cutoffTs == null && !fromCache.isEmpty()) {
                cutoffTs = fromCache.stream()
                        .map(ChatHistory::getCreatedAt)
                        .filter(Objects::nonNull)
                        .map(Instant::toEpochMilli)
                        .min(Long::compareTo)
                        .orElse(null);
            }

            List<ChatMessageEntity> dbMsgs;
            if (cutoffTs != null) {
                dbMsgs = chatMessageRepository.getThreadPageBefore(
                        currentUserId, targetUserId, Instant.ofEpochMilli(cutoffTs), page
                );
            } else {
                dbMsgs = chatMessageRepository.getThreadPageNoBefore(currentUserId, targetUserId, page);
            }

            List<ChatHistory> fromDb = dbMsgs.stream().map(m -> {
                ChatHistory dto = new ChatHistory();
                dto.setId(m.getId());
                dto.setSenderId(m.getSenderId());
                dto.setRecipientId(m.getRecipientId());
                dto.setContent(m.getContent());
                dto.setStatus(m.getStatus().toString());
                dto.setCreatedAt(m.getCreatedAt());
                dto.setDeliveredAt(m.getDeliveredAt());
                dto.setReadAt(m.getReadAt());
                dto.setChatReactionHistoryList(List.of());
                return dto;
            }).toList();

            // 3) Merge newest→oldest, de-duplicated by id
            LinkedHashMap<Long, ChatHistory> merged = new LinkedHashMap<>();
            fromCache.forEach(m -> merged.putIfAbsent(m.getId(), m));
            fromDb.forEach(m -> merged.putIfAbsent(m.getId(), m));
            return new ArrayList<>(merged.values());
        }


    }
