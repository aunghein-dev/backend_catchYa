package com.catch_ya_group.catch_ya.controller.otp;

import com.catch_ya_group.catch_ya.service.otp.SMSPohService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class OTPController {

    private final SMSPohService smsPohService;

    @PostMapping("/request-otp")
    public ResponseEntity<?> requestOtp(@RequestParam String phoneNo) {
        try {
            smsPohService.sendOtp(phoneNo); // sends OTP & persists in DB
            return ResponseEntity.ok(Map.of("message", "OTP sent successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to send OTP"));
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestParam String phoneNo, @RequestParam String otpCode) {
        boolean isValid = smsPohService.verifyOtp(phoneNo, otpCode);
        if (isValid) {
            return ResponseEntity.ok(Map.of("message", "OTP verified successfully"));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Invalid or expired OTP"));
    }
}
