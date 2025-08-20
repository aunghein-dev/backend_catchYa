package com.catch_ya_group.catch_ya.modal.chatpayload;

import lombok.Data;

import java.util.Date;

@Data
public class RecentMessageResponse {
    private Long otherUserId;
    private String otherProImgUrl;
    private String otherFullName;
    private String otherUniqueName;
    private String otherPhoneNo;
    private String chatContent;
    private Date createdAt;
    private Long senderId;
    private Long recipientId;
}
