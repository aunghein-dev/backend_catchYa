package com.catch_ya_group.catch_ya.modal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StatusCreateRequest {
    private Long userId;
    private String content;
    private List<String> hashKeywords;
}
