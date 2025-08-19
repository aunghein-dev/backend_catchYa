package com.catch_ya_group.catch_ya.service.notification;

import com.catch_ya_group.catch_ya.modal.entity.Notification;
import com.catch_ya_group.catch_ya.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public List<Notification> readNotifications(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserId(userId);
        notifications.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(notifications);
        return notifications;
    }

    public List<Notification> getNotificationForUser(Long userId) {
        return notificationRepository.findByUserId(userId);
    }
}
