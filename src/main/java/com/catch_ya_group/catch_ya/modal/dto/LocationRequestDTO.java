package com.catch_ya_group.catch_ya.modal.dto;

import lombok.Data;

@Data
public class LocationRequestDTO {
    private Long userId;
    private double longitude;
    private double latitude;
}
