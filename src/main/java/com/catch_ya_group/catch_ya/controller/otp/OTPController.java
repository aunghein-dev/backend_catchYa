package com.catch_ya_group.catch_ya.controller.otp;

import com.catch_ya_group.catch_ya.modal.entity.OtpRequest;
import com.catch_ya_group.catch_ya.service.otp.OTPRequestService;
import com.catch_ya_group.catch_ya.service.otp.SMSPohService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("public/v1/otp")
@RequiredArgsConstructor
@Tag(
        name = "OTP Service",
        description = "Endpoints for generating, sending, and verifying one-time passwords (OTPs) for secure user authentication."
)
public class OTPController {

    private final SMSPohService smsPohService;
    private final OTPRequestService otpRequestService;

    @Operation(summary = "Request OTP", description = "Sends a one-time password (OTP) to the specified phone number.")
    @PostMapping("/request")
    public ResponseEntity<?> requestOtp(@RequestBody OtpRequest newRequest) {
        try {
            // Step: Create request in sms provider to send back otp request
            smsPohService.sendOtp(newRequest);
            return ResponseEntity.ok(Map.of("message", "OTP sent successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to send OTP"));
        }
    }

    @Operation(summary = "Verify OTP", description = "Verifies the provided OTP code for the given phone number.")
    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(@RequestParam String phoneNo, @RequestParam String otpCode) {
        boolean isValid = smsPohService.verifyOtp(phoneNo, otpCode);
        if (isValid) {
            return ResponseEntity.ok(Map.of("message", "OTP verified successfully"));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Invalid or expired OTP"));
    }
}
