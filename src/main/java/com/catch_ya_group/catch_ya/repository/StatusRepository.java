package com.catch_ya_group.catch_ya.repository;

import com.catch_ya_group.catch_ya.modal.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface StatusRepository extends JpaRepository<Status, Long> {

    List<Status> findByUserId(Long userId);

    @Query("SELECT s FROM Status s WHERE LOWER(s.content) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Status> findByContentContaining(String keyword);

    @Query("SELECT s FROM Status s JOIN s.hashKeywords k WHERE LOWER(k) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Status> findByKeyword(String keyword);
}
