package com.catch_ya_group.catch_ya.modal.dto;

public record ProfileUpdaterRequest (
        Long userId,
        String fullName,
        String uniqueName
) {}