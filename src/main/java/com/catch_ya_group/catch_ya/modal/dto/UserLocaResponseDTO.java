package com.catch_ya_group.catch_ya.modal.dto;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLocaResponseDTO {

    private Long userId;
    private double longitude;
    private double latitude;
    private String phoneNo;
    private String uniqueName;
    private String fullName;
    private String proPicsImgUrl;
    private String createdAt;
    private Integer viewedCnt;

}
