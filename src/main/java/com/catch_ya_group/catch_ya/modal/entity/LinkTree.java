package com.catch_ya_group.catch_ya.modal.entity;


import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LinkTree {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Hidden
    private Long rowId;
    private Long userId;

    private String tiktokUrl;
    private String facebookUrl;
    private String instagramUrl;
    private String telegramUrl;
}
