package com.catch_ya_group.catch_ya.repository;

import com.catch_ya_group.catch_ya.modal.dto.ProfilePhotoReponse;
import com.catch_ya_group.catch_ya.modal.entity.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StatusRepository extends JpaRepository<Status, Long> {

    // For one user, newest first:
    Page<Status> findByUserId(Long userId, Pageable pageable);
    List<Status> findByUserIdOrderByStatusDateTimeDescStatusIdDesc(Long userId);

    // Text search (newest first)
    @Query("""
         SELECT s FROM Status s
         WHERE LOWER(s.content) LIKE LOWER(CONCAT('%', :keyword, '%'))
         ORDER BY s.statusDateTime DESC, s.statusId DESC
         """)
    List<Status> findByContentContainingNewest(String keyword);

    // Keyword search (newest first)
    @Query("""
         SELECT s FROM Status s JOIN s.hashKeywords k
         WHERE LOWER(k) LIKE LOWER(CONCAT('%', :keyword, '%'))
         ORDER BY s.statusDateTime DESC, s.statusId DESC
         """)
    List<Status> findByKeywordNewest(String keyword);

    @Query(value = """
            SELECT t.status_id ,s.image_url , t.status_date_time
            FROM status_images s
            LEFT JOIN status t
            ON t.status_id = s.status_id
            WHERE t.user_id = :userId
            ORDER BY status_date_time desc;
            """, nativeQuery = true)
    List<ProfilePhotoReponse> getPhotoByUserId(@Param("userId") Long userId);
}
