package com.catch_ya_group.catch_ya.repository;

import com.catch_ya_group.catch_ya.modal.entity.LinkTree;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LinkTreeRepository extends JpaRepository<LinkTree, Long> {

    LinkTree findByUserId(Long userId);

    boolean existsByUserId(Long userId);

}
