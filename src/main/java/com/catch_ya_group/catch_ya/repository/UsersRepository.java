package com.catch_ya_group.catch_ya.repository;

import com.catch_ya_group.catch_ya.modal.chatpayload.UserSummary;
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
}
