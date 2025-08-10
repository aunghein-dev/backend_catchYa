package com.catch_ya_group.catch_ya.controller.otp;

import com.catch_ya_group.catch_ya.modal.entity.OTPRequest;
import com.catch_ya_group.catch_ya.service.otp.OTPRequestService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/otp")
public class OTPRequestController {

    private final OTPRequestService otpService;

    public OTPRequestController(OTPRequestService otpService) {
        this.otpService = otpService;
    }

    @PostMapping("/create")
    public OTPRequest createOTP(@RequestParam Long phoneNo, @RequestParam String requestId) {
        return otpService.createOTPRequest(phoneNo, requestId);
    }
}