package com.catch_ya_group.catch_ya.modal.dto;

public record PasswordChangeRequest(
        Long userId,
        String oldPassword,
        String newPassword
) {}

