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
public class Leaderboard {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Hidden
    private Long leaderboardId;
    private Long viewedCnt;
}
