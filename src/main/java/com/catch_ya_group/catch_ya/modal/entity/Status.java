package com.catch_ya_group.catch_ya.modal.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(
        name = "status",
        indexes = {
                @Index(name = "idx_status_user_id", columnList = "user_id"),
                @Index(name = "idx_status_datetime", columnList = "status_date_time")
        }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Status {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long statusId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(columnDefinition = "text")
    private String content;

    @Column(name = "status_date_time")
    private LocalDateTime statusDateTime;

    @ElementCollection
    @CollectionTable(name = "status_hash_keywords", joinColumns = @JoinColumn(name = "status_id"))
    @Column(name = "keyword")
    private List<String> hashKeywords;

    @ElementCollection
    @CollectionTable(name = "status_images", joinColumns = @JoinColumn(name = "status_id"))
    @Column(name = "image_url")
    private List<String> images;

    @PrePersist
    void prePersist() {
        if (statusDateTime == null) statusDateTime = LocalDateTime.now();
    }
}
