package com.catch_ya_group.catch_ya.service.leaderboard;

import com.catch_ya_group.catch_ya.modal.dto.BoardResponse;
import com.catch_ya_group.catch_ya.modal.entity.Leaderboard;
import com.catch_ya_group.catch_ya.modal.entity.LeaderboardHistory;
import com.catch_ya_group.catch_ya.modal.entity.Notification;
import com.catch_ya_group.catch_ya.modal.entity.Users;
import com.catch_ya_group.catch_ya.modal.projection.NotificationType;
import com.catch_ya_group.catch_ya.modal.projection.UserLocaResponseProjection;
import com.catch_ya_group.catch_ya.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaderboardHistoryService {

    private final LeaderboardHistoryRepository leaderboardHistoryRepository;
    private final LeaderboardRepository leaderboardRepository;
    private final UsersRepository usersRepository;
    private final UserLocaRepository userLocaRepository;
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public LeaderboardHistory viewProfileAction(Long fromUserId, Long toUserId) {
        System.out.println(alreadyViewedPeerToPeer(fromUserId, toUserId));

        // Get the viewing user's details for the notification message
        Users fromUser = usersRepository.findById(fromUserId)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + fromUserId + " not found."));
        String fromUserUrl = fromUser.getUserInfos().getProPicsImgUrl();
        String notificationContent = fromUser.getUserInfos().getFullName() + " viewed your profile.";

        // Logic to update the leaderboard history and count
        if (alreadyViewedPeerToPeer(fromUserId, toUserId)) {
            LeaderboardHistory history = leaderboardHistoryRepository.getByPeerToPeer(fromUserId, toUserId);
            history.setPeerToPeerCnt(
                    (history.getPeerToPeerCnt() == null ? 0 : history.getPeerToPeerCnt()) + 1
            );
            leaderboardHistoryRepository.save(history);
        } else {
            LeaderboardHistory newHistory = LeaderboardHistory.builder()
                    .rowId(null)
                    .fromUserId(fromUserId)
                    .toUserId(toUserId)
                    .peerToPeerCnt(1L)
                    .build();
            leaderboardHistoryRepository.save(newHistory);

            Long leaderboardId = usersRepository.findById(toUserId)
                    .map(u -> u.getLeaderboard().getLeaderboardId())
                    .orElse(null);

            if (leaderboardId != null) {
                Leaderboard viewedPeer = leaderboardRepository.getReferenceById(leaderboardId);
                viewedPeer.setViewedCnt(viewedPeer.getViewedCnt() + 1);
                leaderboardRepository.save(viewedPeer);
            }
        }

        // 1. Create and save a new notification record in the database
        Notification newNotification = Notification.builder()
                .userId(toUserId)
                .lastContentMakerUrl(fromUserUrl)
                .lastContentMessage(notificationContent)
                .notificationType(NotificationType.PROFILE_VIEW)
                .isRead(false)
                .notiDateTime(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .build();
        notificationRepository.save(newNotification);

        // 2. Send the real-time notification to the specific toUserId
        // The client should subscribe to '/user/{toUserId}/queue/notifications'
        // Spring's SimpleMessageTemplate handles the user-specific routing
        messagingTemplate.convertAndSendToUser(
                toUserId.toString(),
                "/queue/notifications",
                newNotification
        );

        return leaderboardHistoryRepository.getByPeerToPeer(fromUserId, toUserId);
    }

    public boolean alreadyViewedPeerToPeer(Long fromUserId, Long toUserId){
        return leaderboardHistoryRepository.alreadyViewedPeerToPeer(fromUserId,toUserId);
    }

    public List<UserLocaResponseProjection> getViewersOfUser(Long userId) {
        return userLocaRepository.getAllUsersLoca()
                .stream().filter(u -> getViewersIdsOfUser(userId).contains(u.getUserId()))
                .toList();
    }

    public List<Long> getViewersIdsOfUser(Long userId){
        return leaderboardHistoryRepository.getViewersIdsOfUser(userId);
    }

    public List<BoardResponse> getWholeBoard() {
        return leaderboardHistoryRepository.getWholeBoard();
    }
}
