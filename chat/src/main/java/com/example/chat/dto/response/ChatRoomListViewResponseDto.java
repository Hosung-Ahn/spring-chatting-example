package com.example.chat.dto.response;

import com.example.chat.dto.ChatRoomListInfoDto;
import lombok.Data;

import java.util.List;

@Data
public class ChatRoomListViewResponseDto {
    List<ChatRoomListInfoDto> chatRoomList;

    public ChatRoomListViewResponseDto(List<ChatRoomListInfoDto> chatRoomList) {
        this.chatRoomList = chatRoomList;
    }
}
