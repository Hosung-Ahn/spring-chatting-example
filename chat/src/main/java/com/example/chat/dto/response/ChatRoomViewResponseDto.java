package com.example.chat.dto.response;

import com.example.chat.domain.chat.ChatRoom;

import java.util.List;

public class ChatRoomViewResponseDto {
    private String title;
    private Long ownerId;
    private List<Long> members;
    private List<ChatRoom.Message> messages;
    private Integer unreadMessageCount;

    public ChatRoomViewResponseDto(String title, Long ownerId,
                                   List<Long> members,
                                   List<ChatRoom.Message> messages,
                                   Integer unreadMessageCount) {
        this.title = title;
        this.ownerId = ownerId;
        this.members = members;
        this.messages = messages;
        this.unreadMessageCount = unreadMessageCount;
    }
}
