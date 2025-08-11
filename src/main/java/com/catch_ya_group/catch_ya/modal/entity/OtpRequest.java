package com.catch_ya_group.catch_ya.modal.entity;

import com.catch_ya_group.catch_ya.modal.OTPVerification;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "otp_request_seq")
    @SequenceGenerator(name = "otp_request_seq", sequenceName = "otp_request_seq", allocationSize = 1)
    private Long rowId;

    private String phoneNo;
    private String requestId;
    private String deviceId;
    private String date;

    @Enumerated(EnumType.STRING)
    private OTPVerification status;

}
