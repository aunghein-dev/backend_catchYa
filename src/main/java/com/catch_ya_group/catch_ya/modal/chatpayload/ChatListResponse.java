package com.catch_ya_group.catch_ya.modal.chatpayload;

import lombok.Data;

import java.util.List;

@Data
public class ChatListResponse {

    private String mineProImgUrl;
    private List<RecentMessageResponse> recentMessageResponse;
}
