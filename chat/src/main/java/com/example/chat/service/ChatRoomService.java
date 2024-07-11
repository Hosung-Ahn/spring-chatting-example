package com.example.chat.service;

import com.example.chat.domain.chat.ChatRoom;
import com.example.chat.dto.request.ChatRoomCreateRequestDto;
import com.example.chat.repository.ChatRoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;

    public String create(ChatRoomCreateRequestDto dto) {
        ChatRoom chatRoom = new ChatRoom(dto.getTitle(), dto.getOwnerId(), dto.getMemberIds());
        chatRoomRepository.save(chatRoom);
        return chatRoom.getId();
    }

}
