package com.catch_ya_group.catch_ya.modal.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.Instant;

@Entity
@Table(name = "message_reaction",
        uniqueConstraints = @UniqueConstraint(name="uq_msg_user", columnNames={"message_id","user_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MessageReaction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id")
    private ChatMessageEntity message;

    @Column(name="user_id", nullable = false)
    private Long userId; // who reacted

    @Column(length = 8, nullable = false)
    private String emoji; // 👍 ❤️ 😂 😮 😢 😡 etc.

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
}