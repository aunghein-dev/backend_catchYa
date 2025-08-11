package com.catch_ya_group.catch_ya.modal.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfos {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long userInfoId;

    private String uniqueName;
    private String fullName;
    private String proPicsImgUrl;
    private String coverImgUrl;
    private String createdAt;
}

