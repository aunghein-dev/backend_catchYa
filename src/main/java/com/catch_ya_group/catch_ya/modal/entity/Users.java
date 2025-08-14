package com.catch_ya_group.catch_ya.modal.entity;


import io.swagger.v3.oas.annotations.Hidden;
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
    @SequenceGenerator(
            name = "users_seq",
            sequenceName = "users_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "users_seq"
    )
    @Hidden
    private Long userId;

    private String phoneNo;
    private String password;

    @Column(name = "unique_name", unique = true)
    private String uniqueName;

    @ManyToOne
    @JoinColumn(name = "user_info_id")
    @Hidden
    private UserInfos userInfos;

    @ManyToOne
    @JoinColumn(name = "leaderboard_id")
    @Hidden
    private Leaderboard leaderboard;
}