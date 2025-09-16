package com.catch_ya_group.catch_ya.modal.dto;

public record InfoUserStatusWithLocation(
        Long userId,
        String phoneNo,
        String uniqueName,
        String coverImgUrl,
        String createdAt,
        String fullName,
        String proPicsImgUrl,
        Double latitude,
        Double longitude,
        Double distanceInMiles
) {}