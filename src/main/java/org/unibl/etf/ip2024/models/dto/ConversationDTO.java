package org.unibl.etf.ip2024.models.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class ConversationDTO {
    private Integer userId;
    private String avatarUrl;
    private String username;
    private String lastMessage;
    private Timestamp lastMessageTime;
    private Boolean unread;
}
