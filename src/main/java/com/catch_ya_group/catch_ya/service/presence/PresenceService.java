package com.catch_ya_group.catch_ya.service.presence;

import com.catch_ya_group.catch_ya.modal.presence.PresenceEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PresenceService {
    private final StringRedisTemplate redis;
    private final SimpMessagingTemplate broker;

    // Client pings ~30s → keep online for 75s to cover hiccups
    private static final Duration ONLINE_TTL = Duration.ofSeconds(75);

    private static final String ONLINE_PREFIX = "presence:online:";
    private static final String LAST_PREFIX   = "presence:last:";

    private String onlineKey(long userId) { return ONLINE_PREFIX + userId; }
    private String lastKey(long userId)   { return LAST_PREFIX + userId; }

    /** Called by @MessageMapping("/presence.ping") */
    public void ping(long userId) {
        long now = System.currentTimeMillis();

        boolean wasOnline = Boolean.TRUE.equals(redis.hasKey(onlineKey(userId)));

        // Mark online (ephemeral) + update last-seen
        redis.opsForValue().set(onlineKey(userId), "1", ONLINE_TTL);
        redis.opsForValue().set(lastKey(userId), String.valueOf(now));

        // Broadcast only when offline -> online
        if (!wasOnline) {
            broker.convertAndSend("/topic/presence",
                    new PresenceEvent(userId, true, now));
        }
    }

    /** Optional: mark offline when we detect disconnect */
    public void markOffline(long userId) {
        long now = System.currentTimeMillis();
        redis.opsForValue().set(lastKey(userId), String.valueOf(now));
        redis.delete(onlineKey(userId));
        broker.convertAndSend("/topic/presence",
                new PresenceEvent(userId, false, now));
    }

    public boolean isOnline(long userId) {
        return Boolean.TRUE.equals(redis.hasKey(onlineKey(userId)));
    }

    public OptionalLong lastSeen(long userId) {
        String v = redis.opsForValue().get(lastKey(userId));
        if (v == null) return OptionalLong.empty();
        try { return OptionalLong.of(Long.parseLong(v)); }
        catch (NumberFormatException e) { return OptionalLong.empty(); }
    }

    /** Batch fetch for chat lists: Map<userId, PresenceEvent> */
    public Map<Long, PresenceEvent> batch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptyMap();

        List<String> onlineKeys = ids.stream().map(this::onlineKey).toList();
        List<String> lastKeys   = ids.stream().map(this::lastKey).toList();

        List<String> onlineVals = redis.opsForValue().multiGet(onlineKeys);
        List<String> lastVals   = redis.opsForValue().multiGet(lastKeys);

        if (onlineVals == null) onlineVals = Collections.nCopies(onlineKeys.size(), null);
        if (lastVals   == null) lastVals   = Collections.nCopies(lastKeys.size(), null);

        Map<Long, PresenceEvent> out = new HashMap<>(ids.size());
        for (int i = 0; i < ids.size(); i++) {
            long id = ids.get(i);
            boolean online = onlineVals.get(i) != null; // key exists → non-null
            long last = 0L;
            String lv = lastVals.get(i);
            if (lv != null) {
                try { last = Long.parseLong(lv); } catch (Exception ignored) {}
            }
            out.put(id, new PresenceEvent(id, online, last));
        }
        return out;
    }

    /** Returns the set of userIds that are currently online (via SCAN). */
    public Set<Long> onlineUserIds() {
        final Set<Long> ids = new HashSet<>();

        redis.execute((RedisConnection conn) -> {
            ScanOptions opts = ScanOptions.scanOptions()
                    .match(ONLINE_PREFIX + "*")
                    .count(1000)
                    .build();
            try (Cursor<byte[]> cur = conn.scan(opts)) {
                while (cur.hasNext()) {
                    String key = new String(cur.next(), StandardCharsets.UTF_8);
                    String idStr = key.substring(ONLINE_PREFIX.length());
                    try {
                        ids.add(Long.parseLong(idStr));
                    } catch (NumberFormatException ignore) {
                        // skip malformed keys
                    }
                }
            }
            return null;
        });

        return ids;
    }

    /** Convenience: full PresenceEvent list for everyone online right now. */
    public List<PresenceEvent> allOnline() {
        Set<Long> ids = onlineUserIds();
        if (ids.isEmpty()) return List.of();
        Map<Long, PresenceEvent> map = batch(new ArrayList<>(ids));
        // (They should all be online, but filter defensively)
        return map.values().stream().filter(PresenceEvent::isOnline).toList();
    }

    // -------------------------------------------
    // DEV-ONLY alternative (simple but blocking):
    // public Set<Long> onlineUserIds() {
    //     Set<String> keys = redis.keys(ONLINE_PREFIX + "*");
    //     if (keys == null || keys.isEmpty()) return Set.of();
    //     Set<Long> out = new HashSet<>();
    //     for (String k : keys) {
    //         try { out.add(Long.parseLong(k.substring(ONLINE_PREFIX.length()))); }
    //         catch (Exception ignore) {}
    //     }
    //     return out;
    // }
    // -------------------------------------------
}
