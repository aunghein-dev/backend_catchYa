package com.catch_ya_group.catch_ya.repository;

import com.catch_ya_group.catch_ya.modal.entity.LeaderboardHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaderboardHistoryRepository extends JpaRepository<LeaderboardHistory, Long> {

    @Query(value = """
        SELECT EXISTS(
            SELECT 1
            FROM leaderboard_history
            WHERE from_user_id = :fromUserId
              AND to_user_id = :toUserId
        )
    """, nativeQuery = true)
    boolean alreadyViewedPeerToPeer(@Param("fromUserId") Long fromUserId, @Param("toUserId") Long toUserId);


    @Query(value = """
        SELECT *
        FROM leaderboard_history
        WHERE from_user_id = :fromUserId
          AND to_user_id = :toUserId
        LIMIT 1
    """, nativeQuery = true)
    LeaderboardHistory getByPeerToPeer(@Param("fromUserId") Long fromUserId, @Param("toUserId") Long toUserId);

    @Query(value = """
          SELECT from_user_id
          FROM leaderboard_history
          WHERE to_user_id = :userId
          """, nativeQuery = true)
    List<Long> getViewersIdsOfUser(@Param("userId") Long userId);
}

