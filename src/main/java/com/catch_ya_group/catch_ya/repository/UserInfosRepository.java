package com.catch_ya_group.catch_ya.repository;

import com.catch_ya_group.catch_ya.modal.entity.UserInfos;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInfosRepository extends JpaRepository<UserInfos, Long> {
}
