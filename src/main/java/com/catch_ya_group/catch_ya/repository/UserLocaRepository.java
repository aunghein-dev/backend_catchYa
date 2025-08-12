package com.catch_ya_group.catch_ya.repository;

import com.catch_ya_group.catch_ya.modal.entity.UserLoca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserLocaRepository extends JpaRepository<UserLoca, Long> {

    @Query(value = """
        SELECT * FROM user_loca
        WHERE ST_DWithin(location, ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography, :distance)
        """, nativeQuery = true)
    List<UserLoca> findNearbyUsers(
            @Param("lon") double longitude,
            @Param("lat") double latitude,
            @Param("distance") double distanceInMeters
    );


    UserLoca findByUserId(Long userId);
}