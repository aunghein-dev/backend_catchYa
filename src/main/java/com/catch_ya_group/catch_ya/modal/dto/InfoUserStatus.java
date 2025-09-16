package com.catch_ya_group.catch_ya.modal.dto;

public record InfoUserStatus(
        Long userId,  // Add this field
        String phoneNo,
        String uniqueName,
        String coverImgUrl,
        String createdAt,
        String fullName,
        String proPicsImgUrl
) {}