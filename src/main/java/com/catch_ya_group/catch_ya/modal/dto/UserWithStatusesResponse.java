package com.catch_ya_group.catch_ya.modal.dto;

import java.util.List;

public record UserWithStatusesResponse(
        InfoUserStatusWithLocation user,
        List<StatusResponse> statuses
) {}