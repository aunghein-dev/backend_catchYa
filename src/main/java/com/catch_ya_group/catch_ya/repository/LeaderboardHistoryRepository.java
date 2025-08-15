package com.catch_ya_group.catch_ya.repository;

import com.catch_ya_group.catch_ya.modal.dto.BoardResponse;
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

    @Query(value = """
        select ROW_NUMBER() OVER (ORDER BY l.viewed_cnt desc) as rank,
               l.viewed_cnt,
               u.user_id,
               u.phone_no,
               u.unique_name,
               i.full_name,
               i.cover_img_url,
               i.pro_pics_img_url
        from\s
        leaderboard l
        left join users u on u.leaderboard_id = l.leaderboard_id
        left join user_infos i on i.user_info_id = u.user_info_id
        order by viewed_cnt desc
        """, nativeQuery = true)
    List<BoardResponse> getWholeBoard();
}

