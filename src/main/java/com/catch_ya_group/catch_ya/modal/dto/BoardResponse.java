package com.catch_ya_group.catch_ya.modal.dto;

public record BoardResponse(
        Long rank,
        Long viewedCnt,
        Long userId,
        String phoneNo,
        String uniqueName,
        String fullName,
        String coverImageUrl,
        String proPicsImgUrl
) {}
