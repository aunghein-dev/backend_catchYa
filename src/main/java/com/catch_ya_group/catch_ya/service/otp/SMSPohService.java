package com.catch_ya_group.catch_ya.service.otp;

import com.catch_ya_group.catch_ya.modal.OTPVerification;
import com.catch_ya_group.catch_ya.modal.entity.OTPRequest;
import com.catch_ya_group.catch_ya.repository.OTPRequestRepository;
import com.catch_ya_group.catch_ya.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SMSPohService {

    private final UserRepo userRepo;
    private final OTPRequestRepository otpRequestRepository;
    private final RestTemplate restTemplate;

    @Value("${smspoh.api.key}")
    private String apiKey;

    @Value("${smspoh.api.secret}")
    private String apiSecret;

    public void sendOtp(String phoneNo) {
        String accessToken = Base64.getEncoder()
                .encodeToString((apiKey + ":" + apiSecret).getBytes());

        String url = String.format(
                "https://v3.smspoh.com/api/otp/request?from=MyApp&to=%s&brand=MyApp&accessToken=%s",
                phoneNo, accessToken
        );

        ResponseEntity<Map> response = restTemplate.postForEntity(url, null, Map.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            String requestId = String.valueOf(response.getBody().get("requestId"));

            OTPRequest otpRequest = new OTPRequest();
            otpRequest.setPhoneNo(Long.valueOf(phoneNo));
            otpRequest.setRequestId(requestId);
            otpRequest.setStatus(OTPVerification.PENDING);

            otpRequestRepository.save(otpRequest); // Persist to DB
        } else {
            throw new RuntimeException("Failed to send OTP");
        }
    }

    public boolean verifyOtp(String phoneNo, String otpCode) {
        // Find the most recent request for that phone number
        OTPRequest otpRequest = otpRequestRepository
                .findTopByPhoneNoOrderByRowIdDesc(Long.valueOf(phoneNo))
                .orElseThrow(() -> new RuntimeException("No OTP request found for this phone number"));

        String accessToken = Base64.getEncoder()
                .encodeToString((apiKey + ":" + apiSecret).getBytes());

        String url = String.format(
                "https://v3.smspoh.com/api/otp/verify?requestId=%s&code=%s&accessToken=%s",
                otpRequest.getRequestId(), otpCode, accessToken
        );

        ResponseEntity<Map> response = restTemplate.postForEntity(url, null, Map.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            otpRequest.setStatus(OTPVerification.VERIFIED);
            otpRequestRepository.save(otpRequest);
            return true;
        }
        return false;
    }
}
