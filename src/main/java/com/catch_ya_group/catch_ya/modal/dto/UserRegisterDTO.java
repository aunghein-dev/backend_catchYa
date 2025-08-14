package com.catch_ya_group.catch_ya.modal.dto;

import com.catch_ya_group.catch_ya.modal.entity.Leaderboard;
import com.catch_ya_group.catch_ya.modal.entity.UserInfos;
import com.catch_ya_group.catch_ya.modal.entity.Users;
import lombok.Data;

@Data
public class UserRegisterDTO {
    private Users newUser;
    private UserInfos userInfos;
    private Leaderboard leaderboard;
}

