package com.catch_ya_group.catch_ya.modal.entity;

import com.catch_ya_group.catch_ya.modal.projection.NotificationType;
import com.catch_ya_group.catch_ya.modal.projection.OTPVerification;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Hidden
    private Long notiId;

    private String notiDateTime;
    private Long userId;
    private String lastContentMakerUrl;
    private String lastContentMessage;
    private boolean isRead;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;
}
