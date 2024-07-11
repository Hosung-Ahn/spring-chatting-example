package com.example.chat.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class ChatRoomCreateRequestDto {
    private Long ownerId;
    private String title;
    List<Long> memberIds;
}
