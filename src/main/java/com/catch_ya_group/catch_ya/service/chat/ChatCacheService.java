package com.catch_ya_group.catch_ya.service.chat;

import com.catch_ya_group.catch_ya.modal.chatpayload.UserSummary;
import com.catch_ya_group.catch_ya.modal.entity.ChatMessageEntity;
import com.catch_ya_group.catch_ya.modal.projection.MessageStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static com.catch_ya_group.catch_ya.service.chat.ChatCacheKeys.*;

@Service
@RequiredArgsConstructor
public class ChatCacheService {

    private final StringRedisTemplate redis;

    // keep last N messages per thread in Redis
    private static final int HOT_TAIL = 200;
    private static final Duration MSG_TTL = Duration.ofDays(7);

    /** Stable thread id for any pair of users */
    public long threadId(long a, long b) {
        long lo = Math.min(a, b);
        long hi = Math.max(a, b);
        return (lo << 32) | hi;
    }

    /** Cache a message (after DB save). Also updates both users' chat list + unread for recipient. */
    public void cacheNewMessage(ChatMessageEntity ent) {
        long sender = ent.getSenderId();
        long rcpt   = ent.getRecipientId();
        long tid    = threadId(sender, rcpt);
        long ts     = ent.getCreatedAt().toEpochMilli();
        String msgId= String.valueOf(ent.getId());

        // 1) Thread index
        String idxKey = threadIdx(tid);
        redis.opsForZSet().add(idxKey, msgId, ts);
        Long size = redis.opsForZSet().zCard(idxKey);
        if (size != null && size > HOT_TAIL + 20) { // small buffer
            redis.opsForZSet().removeRange(idxKey, 0, size - HOT_TAIL - 1);
        }

        // 2) Message payload (Hash)  — all String fields, because StringRedisTemplate
        String mKey = msgHash(msgId);
        Map<String, String> m = new LinkedHashMap<>();
        m.put("id", msgId);
        m.put("clientMessageId", ent.getClientMessageId());
        m.put("senderId", String.valueOf(sender));
        m.put("recipientId", String.valueOf(rcpt));
        m.put("content", ent.getContent());
        m.put("status", ent.getStatus() != null ? ent.getStatus().name() : MessageStatus.SENT.name());
        m.put("createdAt", String.valueOf(ts));
        if (ent.getDeliveredAt() != null) m.put("deliveredAt", String.valueOf(ent.getDeliveredAt().toEpochMilli()));
        if (ent.getReadAt() != null)      m.put("readAt", String.valueOf(ent.getReadAt().toEpochMilli()));
        redis.opsForHash().putAll(mKey, m);
        redis.expire(mKey, MSG_TTL);

        // 3) Update chat list preview rows for BOTH users
        updateChatListForUser(sender, rcpt, ent);
        updateChatListForUser(rcpt,   sender, ent);

        // 4) Increment unread for recipient (recipient's list row will read this)
        redis.opsForValue().increment(unreadKey(rcpt, sender));
    }

    /** Update a user's chat list ZSET + conversation preview HASH. */
    public void updateChatListForUser(long userId, long peerId, ChatMessageEntity ent) {
        long ts = ent.getCreatedAt().toEpochMilli();
        redis.opsForZSet().add(listZset(userId), String.valueOf(peerId), ts);

        Map<String, String> conv = new HashMap<>();
        conv.put("otherUserId", String.valueOf(peerId));
        conv.put("lastMessageId", String.valueOf(ent.getId()));
        conv.put("lastMessageAt", String.valueOf(ts));
        conv.put("lastSenderId", String.valueOf(ent.getSenderId()));
        conv.put("lastText", ent.getContent());
        String unread = Optional.ofNullable(redis.opsForValue().get(unreadKey(userId, peerId))).orElse("0");
        conv.put("unreadCount", unread);
        redis.opsForHash().putAll(convHash(userId, peerId), conv);
    }

    /** On READ ack: zero unread for reader, set lastread marker. */
    public void markRead(long readerId, long otherId, Instant when) {
        long tid = threadId(readerId, otherId);
        redis.opsForValue().set(lastReadKey(readerId, tid), String.valueOf(when.toEpochMilli()));
        redis.opsForValue().set(unreadKey(readerId, otherId), "0");
        redis.opsForHash().put(convHash(readerId, otherId), "unreadCount", "0");
    }

    /** Return recent chat list from Redis. Empty list if nothing cached. */
    public List<Map<String, String>> chatListFromCache(long userId, int rows) {
        Set<String> peers = redis.opsForZSet().reverseRange(listZset(userId), 0, rows - 1);
        if (peers == null || peers.isEmpty()) return List.of();

        List<Map<String, String>> out = new ArrayList<>(peers.size());
        for (String pid : peers) {
            Map<Object, Object> h = redis.opsForHash().entries(convHash(userId, Long.parseLong(pid)));
            if (h != null && !h.isEmpty()) {
                // 🔧 FIX: entries are Strings (not byte[]). Use toString().
                Map<String,String> row = h.entrySet().stream()
                        .collect(Collectors.toMap(
                                e -> String.valueOf(e.getKey()),
                                e -> e.getValue() == null ? null : String.valueOf(e.getValue())
                        ));
                out.add(row);
            }
        }
        return out;
    }

    /** Read hot-tail history for a thread from Redis. */
    public List<Map<String,String>> hotHistoryRaw(long a, long b, Long beforeTs, int limit) {
        long tid = threadId(a, b);
        String idxKey = threadIdx(tid);

        Set<String> msgIds;
        if (beforeTs == null) {
            msgIds = redis.opsForZSet().reverseRange(idxKey, 0, limit - 1);
        } else {
            // 🔧 FIX: (min, max) must be ascending; we want 0 .. beforeTs-1, then reverseRange for newest of that slice
            msgIds = redis.opsForZSet().reverseRangeByScore(idxKey, 0, beforeTs - 1, 0, limit);
        }
        if (msgIds == null || msgIds.isEmpty()) return List.of();

        List<Map<String,String>> result = new ArrayList<>(msgIds.size());
        for (String mid : msgIds) {
            Map<Object, Object> h = redis.opsForHash().entries(msgHash(mid));
            if (h != null && !h.isEmpty()) {
                // 🔧 FIX: same as above — strings, not byte[]
                Map<String,String> row = h.entrySet().stream()
                        .collect(Collectors.toMap(
                                e -> String.valueOf(e.getKey()),
                                e -> e.getValue() == null ? null : String.valueOf(e.getValue())
                        ));
                result.add(row);
            }
        }
        // Already newest-first because we used reverse* calls
        return result;
    }

    public void updateConvProfile(long userId, long peerId, UserSummary p) {
        Map<String, String> conv = new HashMap<>();
        conv.put("otherProImgUrl", p.getProImgUrl());
        conv.put("otherFullName", p.getFullName());
        conv.put("otherUniqueName", p.getUniqueName());
        conv.put("otherPhoneNo", p.getPhoneNo());
        redis.opsForHash().putAll(convHash(userId, peerId), conv);
    }
}
