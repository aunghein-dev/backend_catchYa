package com.catch_ya_group.catch_ya.modal.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_seq")
    private Long userId;

    private String phoneNo;
    private String password;

    @Column(name = "unique_name", unique = true)
    private String uniqueName;

    @ManyToOne
    @JoinColumn(name = "user_info_id")
    private UserInfos userInfos;

    @ManyToOne
    @JoinColumn(name = "leaderboard_id")
    private Leaderboard leaderboard;
}