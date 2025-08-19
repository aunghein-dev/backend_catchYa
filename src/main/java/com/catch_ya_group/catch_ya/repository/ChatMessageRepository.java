package com.catch_ya_group.catch_ya.repository;


import com.catch_ya_group.catch_ya.modal.entity.ChatMessageEntity;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;

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

}