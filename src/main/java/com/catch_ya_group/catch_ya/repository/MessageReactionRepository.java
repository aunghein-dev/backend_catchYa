package com.catch_ya_group.catch_ya.repository;
import com.catch_ya_group.catch_ya.modal.entity.MessageReaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface MessageReactionRepository extends JpaRepository<MessageReaction, Long> {
    Optional<MessageReaction> findByMessage_IdAndUserId(Long messageId, Long userId);
    List<MessageReaction> findByMessage_Id(Long messageId);
}