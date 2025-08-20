package com.catch_ya_group.catch_ya.modal.chatpayload;

public interface RecentMessageRow {
    Long getOther_user_id();
    String getMine_pro_img_url();
    String getOther_full_name();
    String getOther_phone_no();
    String getOther_unique_name();
    String getOther_pro_img_url();
    String getChat_content();
    java.util.Date getCreated_at();
    Long getRecipient_id();
    Long getSender_id();
}