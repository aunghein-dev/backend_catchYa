package com.catch_ya_group.catch_ya.modal.dto;

import com.catch_ya_group.catch_ya.modal.entity.UserInfos;
import com.catch_ya_group.catch_ya.modal.entity.UserLoca;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLocaDTO {

    private Long userLocaId;
    private Long userId;
    private double longitude;
    private double latitude;

    public static UserLocaDTO fromEntity(UserLoca entity) {
        return UserLocaDTO.builder()
                .userLocaId(entity.getUserLocaId())
                .userId(entity.getUserId())
                .longitude(entity.getLocation().getX())
                .latitude(entity.getLocation().getY())
                .build();
    }
}