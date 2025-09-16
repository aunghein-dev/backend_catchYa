package com.catch_ya_group.catch_ya.repository;

import com.catch_ya_group.catch_ya.modal.chatpayload.UserSummary;
import com.catch_ya_group.catch_ya.modal.dto.InfoUserStatus;
import com.catch_ya_group.catch_ya.modal.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {

    Users findByPhoneNo(String phoneNo);

    boolean existsByPhoneNo(String phoneNo);

    boolean existsByUniqueName(String uniqueName);

    @Query(value = """
            select user_id from users where phone_no = :phoneNo
            """, nativeQuery = true)
    Long findUserIdByPhoneNo(@Param("phoneNo") String phoneNo);

    @Query(value = """
        select
            u.user_id               as id,
            i.pro_pics_img_url      as proImgUrl,
            i.full_name             as fullName,
            u.unique_name           as uniqueName,
            u.phone_no              as phoneNo
        from users u
        left join user_infos i on i.user_info_id = u.user_info_id
        where u.user_id in (:ids)
        """,
            nativeQuery = true)
    List<UserSummary> findSummaries(@Param("ids") Collection<Long> ids);

    @Query(value = """
    select u.user_id,
           u.phone_no,
           u.unique_name,
           i.cover_img_url,
           i.created_at,
           i.full_name,
           i.pro_pics_img_url,
           ST_Y(l.location::geometry) as latitude,
           ST_X(l.location::geometry) as longitude,
           ST_Distance(l.location, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)) * 0.000621371 as distance
    from users u
    left join user_infos i on i.user_info_id = u.user_info_id
    left join user_loca l on u.user_id = l.user_id
    where u.user_id in :userIds
    """, nativeQuery = true)
    List<Object[]> getUserInfoStatusWithLocationBatch(@Param("userIds") List<Long> userIds,
                                                      @Param("latitude") Double latitude,
                                                      @Param("longitude") Double longitude);
}
