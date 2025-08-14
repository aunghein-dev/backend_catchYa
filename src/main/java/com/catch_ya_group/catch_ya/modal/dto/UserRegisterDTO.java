package com.catch_ya_group.catch_ya.modal.dto;

import com.catch_ya_group.catch_ya.modal.entity.Leaderboard;
import com.catch_ya_group.catch_ya.modal.entity.UserInfos;
import com.catch_ya_group.catch_ya.modal.entity.Users;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserRegisterDTO {
    private Users newUser;

    @Schema(hidden = true)
    private UserInfos userInfos;

    @Schema(hidden = true)
    private Leaderboard leaderboard;
}

