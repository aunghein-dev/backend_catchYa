package com.catch_ya_group.catch_ya.repository;

import com.catch_ya_group.catch_ya.modal.entity.InstanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface InstanceStatusRepository extends JpaRepository<InstanceStatus, Long> {

    InstanceStatus getInstanceStatusByUserId(Long userId);

    @Query(value = """
        SELECT EXISTS(
            SELECT 1
            FROM instance_status
            WHERE user_id = :userId
        )
    """, nativeQuery = true)
    boolean checkExistInstanceStatus(@Param("userId") Long userId);


    @Modifying
    @Transactional
    @Query(value = """
            DELETE FROM instance_status
            WHERE status_date <= NOW() - INTERVAL '24 HOURS'
        """, nativeQuery = true)
    int deleteExpiredStatuses();


}
