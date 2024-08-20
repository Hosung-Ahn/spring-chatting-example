package com.example.chat.dto.websocket;

import lombok.Data;

@Data
public class ChatMessageReceiveDto {
    Long senderId;
    String content;
}
