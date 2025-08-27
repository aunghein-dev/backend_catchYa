package com.catch_ya_group.catch_ya.modal.entity;

import com.catch_ya_group.catch_ya.modal.projection.MessageStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "chat_message",
        indexes = {
                @Index(name="idx_sender_recipient_time", columnList="sender_id,recipient_id,created_at"),
                @Index(name="idx_recipient_sender_time", columnList="recipient_id,sender_id,created_at"),
                @Index(name="idx_client_msg_id", columnList="client_message_id", unique = true)
        })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ChatMessageEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="client_message_id", length = 50, nullable = false, unique = true)
    private String clientMessageId; // UUID from client

    @Column(name="sender_id", nullable = false)
    private Long senderId;

    @Column(name="recipient_id", nullable = false)
    private Long recipientId;

    @Column(nullable = false, length = 4000)
    private String content;

    @CreationTimestamp
    @Column(name="created_at", updatable = false, nullable = false)
    private Instant createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private MessageStatus status = MessageStatus.SENT;

    private Instant deliveredAt;
    private Instant readAt;
}