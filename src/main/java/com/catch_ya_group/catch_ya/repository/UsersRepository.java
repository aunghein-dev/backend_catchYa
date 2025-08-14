package com.catch_ya_group.catch_ya.repository;

import com.catch_ya_group.catch_ya.modal.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {

    Users findByPhoneNo(String phoneNo);

    boolean existsByPhoneNo(String phoneNo);

    boolean existsByUniqueName(String uniqueName);

    @Query(value = """
            select user_id from users where phone_no = :phoneNo
            """, nativeQuery = true)
    Long findUserIdByPhoneNo(@Param("phoneNo") String phoneNo);
}
