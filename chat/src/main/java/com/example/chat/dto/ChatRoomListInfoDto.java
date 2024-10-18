package com.example.chat.dto;

import com.example.chat.domain.chat.ChatRoom;
import lombok.Data;

@Data
public class ChatRoomListInfoDto {
    private String chatRoomId;
    private String title;
    private Integer memberCount;
    private Integer unreadCount;
    private Long ownerId;
    private ChatRoom.Message lastMessage;

    public ChatRoomListInfoDto(ChatRoom chatRoom, Integer unreadCount) {
        this.chatRoomId = chatRoom.getId();
        this.title = chatRoom.getTitle();
        this.memberCount = chatRoom.getParticipants().size();
        this.ownerId = chatRoom.getOwnerId();
        this.unreadCount = unreadCount;
        this.lastMessage = chatRoom.getRecentMessage();
    }
}
