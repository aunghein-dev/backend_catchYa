package com.catch_ya_group.catch_ya.modal.dto;

import java.time.LocalDateTime;
import java.util.List;

public record StatusWithUserAndLocationResponse(
        Long statusId,
        Long userId,
        InfoUserStatusWithLocation user,
        String content,
        LocalDateTime statusDateTime,
        List<String> hashKeywords,
        List<String> images
) {}