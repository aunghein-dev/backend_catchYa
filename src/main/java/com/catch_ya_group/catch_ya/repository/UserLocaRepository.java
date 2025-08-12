package com.catch_ya_group.catch_ya.repository;

import com.catch_ya_group.catch_ya.modal.entity.UserLoca;
import com.catch_ya_group.catch_ya.modal.projection.UserLocaResponseProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserLocaRepository extends JpaRepository<UserLoca, Long> {

    @Query(value = """
        SELECT l.user_id as userId,
               ST_X(l.location::geometry) as longitude,
               ST_Y(l.location::geometry) as latitude,
               u.phone_no as phoneNo,
               u.unique_name as uniqueName,
               i.full_name as fullName,
               i.pro_pics_img_url as proPicsImgUrl,
               i.created_at as createdAt,
               lb.viewed_cnt as viewedCnt
        FROM user_loca l
        LEFT JOIN users u ON u.user_id = l.user_id
        LEFT JOIN user_infos i ON i.user_info_id = u.user_info_id
        LEFT JOIN leaderboard lb ON lb.leaderboard_id = u.user_id
        WHERE ST_DWithin(l.location, ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography, :distance)
          AND l.user_id != :userId
        """, nativeQuery = true)
    List<UserLocaResponseProjection> findNearbyUsers(
            @Param("lon") double longitude,
            @Param("lat") double latitude,
            @Param("distance") double distanceInMeters,
            @Param("userId") Long userId
    );


    UserLoca findByUserId(Long userId);
}
