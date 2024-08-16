package com.example.chat.service;

import com.example.chat.domain.chat.ChatRoom;
import com.example.chat.dto.request.ChatRoomCreateRequestDto;
import com.example.chat.repository.ChatProfileRepository;
import com.example.chat.repository.ChatRoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatProfileRepository chatProfileRepository;

    public String create(ChatRoomCreateRequestDto dto) {
        ChatRoom chatRoom = new ChatRoom(dto.getTitle(), dto.getOwnerId(), dto.getMemberIds());
        String chatRoomId = chatRoomRepository.save(chatRoom);

        for (Long memberId : dto.getMemberIds()) {
            chatProfileRepository.appendChatRoom(memberId, chatRoomId);
        }
        return chatRoom.getId();
    }

    public void joinChatRoom(String chatRoomId, Long memberId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId);
        chatRoom.getParticipants().add(memberId);
        chatRoomRepository.save(chatRoom);
        chatProfileRepository.appendChatRoom(memberId, chatRoomId);
    }



    public void exitChatRoom(String chatRoomId, Long memberId) {

    }
}
