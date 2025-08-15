package com.catch_ya_group.catch_ya.service.leaderboard;

import com.catch_ya_group.catch_ya.modal.dto.BoardResponse;
import com.catch_ya_group.catch_ya.modal.entity.Leaderboard;
import com.catch_ya_group.catch_ya.modal.entity.LeaderboardHistory;
import com.catch_ya_group.catch_ya.modal.projection.UserLocaResponseProjection;
import com.catch_ya_group.catch_ya.repository.LeaderboardHistoryRepository;
import com.catch_ya_group.catch_ya.repository.LeaderboardRepository;
import com.catch_ya_group.catch_ya.repository.UserLocaRepository;
import com.catch_ya_group.catch_ya.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaderboardHistoryService {

    private final LeaderboardHistoryRepository leaderboardHistoryRepository;
    private final LeaderboardRepository leaderboardRepository;
    private final UsersRepository usersRepository;
    private final UserLocaRepository userLocaRepository;

    public LeaderboardHistory viewProfileAction(Long fromUserId, Long toUserId) {
        System.out.println(alreadyViewedPeerToPeer(fromUserId, toUserId));
        if(alreadyViewedPeerToPeer(fromUserId,toUserId)){
           LeaderboardHistory history = leaderboardHistoryRepository.getByPeerToPeer(fromUserId,toUserId);
            history.setPeerToPeerCnt(
                    (history.getPeerToPeerCnt() == null ? 0 : history.getPeerToPeerCnt()) + 1
            );
            leaderboardHistoryRepository.save(history);
            return history;
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

            if(leaderboardId != null){
                Leaderboard viewedPeer = leaderboardRepository.getReferenceById(leaderboardId);
                viewedPeer.setViewedCnt(viewedPeer.getViewedCnt() + 1);
                leaderboardRepository.save(viewedPeer);
            }

            return newHistory;
        }
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
