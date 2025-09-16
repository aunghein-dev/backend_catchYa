package com.catch_ya_group.catch_ya.modal.dto;

import java.time.LocalDateTime;
import java.util.List;

public record StatusResponse(
        Long statusId,
        String content,
        LocalDateTime statusDateTime,
        List<String> hashKeywords,
        List<String> images
) {}