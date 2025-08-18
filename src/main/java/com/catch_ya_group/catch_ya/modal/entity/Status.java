package com.catch_ya_group.catch_ya.modal.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Status {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long statusId;

    private Long userId;

    private String content;

    // Store keywords as JSON string
    @ElementCollection
    private List<String> hashKeywords;

    // Store image URLs as JSON string
    @ElementCollection
    private List<String> images;
}
