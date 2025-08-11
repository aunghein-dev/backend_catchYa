package com.catch_ya_group.catch_ya.repository;

import com.catch_ya_group.catch_ya.modal.entity.OtpRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OTPRequestRepository extends JpaRepository<OtpRequest, Long> {
    Optional<OtpRequest> findTopByPhoneNoOrderByRowIdDesc(String phoneNo);
}
