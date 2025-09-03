package com.catch_ya_group.catch_ya.modal.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Status {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long statusId;

    private Long userId;

    private String content;
    private LocalDateTime statusDateTime;

    // Store keywords as JSON string
    @ElementCollection
    private List<String> hashKeywords;

    // Store image URLs as JSON string
    @ElementCollection
    private List<String> images;
}
