package com.catch_ya_group.catch_ya.service.otp;

import com.catch_ya_group.catch_ya.modal.OTPVerification;
import com.catch_ya_group.catch_ya.modal.entity.OtpRequest;
import com.catch_ya_group.catch_ya.repository.OTPRequestRepository;
import org.springframework.stereotype.Service;

@Service
public class OTPRequestService {

    private final OTPRequestRepository repository;

    public OTPRequestService(OTPRequestRepository repository) {
        this.repository = repository;
    }

    public OtpRequest createOTPRequest(String phoneNo, String requestId) {
        OtpRequest otpRequest = new OtpRequest();
        otpRequest.setPhoneNo(phoneNo);
        otpRequest.setRequestId(requestId);
        otpRequest.setStatus(OTPVerification.PENDING);
        return repository.save(otpRequest);
    }
}