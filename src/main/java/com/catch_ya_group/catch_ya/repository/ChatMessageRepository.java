package com.catch_ya_group.catch_ya.repository;


import com.catch_ya_group.catch_ya.modal.chatpayload.ChatListResponse;
import com.catch_ya_group.catch_ya.modal.chatpayload.RecentMessageRow;
import com.catch_ya_group.catch_ya.modal.entity.ChatMessageEntity;
import com.catch_ya_group.catch_ya.modal.entity.MessageReaction;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {
    @Query("""
    select m from ChatMessageEntity m
     where (m.senderId=:a and m.recipientId=:b)
        or (m.senderId=:b and m.recipientId=:a)
     order by m.createdAt asc
  """)
    Page<ChatMessageEntity> findConversation(Long a, Long b, Pageable pageable);

    Optional<ChatMessageEntity> findByClientMessageId(String clientMessageId);


    @Query(value = "SELECT * FROM get_chat_list(:userId, :rows)", nativeQuery = true)
    List<RecentMessageRow> getRecentChatListRows(@Param("userId") Long userId, @Param("rows") int rows);

    @Query(value = "SELECT * FROM get_chat_thread(:currentUserId, :targetUserId)", nativeQuery = true)
    List<ChatMessageEntity> getChatThread(@Param("currentUserId") Long currentUserId,@Param("targetUserId") Long targetUserId);

    @Query(value = "SELECT * FROM get_message_reactions_between(:currentUserId, :targetUserId)", nativeQuery = true)
    List<MessageReaction> getChatReactions(@Param("currentUserId") Long currentUserId, @Param("targetUserId") Long targetUserId);

}