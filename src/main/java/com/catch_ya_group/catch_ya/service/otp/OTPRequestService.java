package com.catch_ya_group.catch_ya.service.otp;

import com.catch_ya_group.catch_ya.modal.OTPVerification;
import com.catch_ya_group.catch_ya.modal.entity.OTPRequest;
import com.catch_ya_group.catch_ya.repository.OTPRequestRepository;
import org.springframework.stereotype.Service;

@Service
public class OTPRequestService {

    private final OTPRequestRepository repository;

    public OTPRequestService(OTPRequestRepository repository) {
        this.repository = repository;
    }

    public OTPRequest createOTPRequest(Long phoneNo, String requestId) {
        OTPRequest otpRequest = new OTPRequest();
        otpRequest.setPhoneNo(phoneNo);
        otpRequest.setRequestId(requestId);
        otpRequest.setStatus(OTPVerification.PENDING);
        return repository.save(otpRequest);
    }
}