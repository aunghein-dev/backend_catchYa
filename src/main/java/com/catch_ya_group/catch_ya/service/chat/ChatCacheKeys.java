package com.catch_ya_group.catch_ya.service.chat;

final class ChatCacheKeys {
    static String threadIdx(long tid)           { return "chat:idx:" + tid; }              // ZSET of msgIds (score = createdAt ms)
    static String msgHash(String msgId)         { return "chat:msg:" + msgId; }            // HASH per message
    static String listZset(long userId)         { return "chat:list:" + userId; }          // ZSET of peerIds (score = lastMessageAt)
    static String convHash(long userId, long p) { return "chat:conv:" + userId + ":" + p; }// HASH per conversation row
    static String unreadKey(long userId, long p){ return "chat:unread:" + userId + ":" + p; }
    static String lastReadKey(long userId,long tid){ return "chat:lastread:" + userId + ":" + tid; }
    private ChatCacheKeys(){}
}
