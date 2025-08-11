package com.catch_ya_group.catch_ya.modal.entity;
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
    private Long leaderboardId;

    private String uniqueName;
    private String viewedCnt;
}
