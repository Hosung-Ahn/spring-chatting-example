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
        // 채팅방 객체 생성
        ChatRoom chatRoom = new ChatRoom(dto.getTitle(), dto.getOwnerId(), dto.getMemberIds());
        String chatRoomId = chatRoomRepository.save(chatRoom);

        // 채팅방에 참여자 추가
        for (Long memberId : dto.getMemberIds()) {
            chatProfileRepository.appendChatRoom(memberId, chatRoomId);
        }
        return chatRoom.getId();
    }

    public void joinChatRoom(String chatRoomId, Long memberId) {
        ChatRoom chatRoom = chatRoomRepository.findByIdWithOutMessages(chatRoomId);
        chatRoom.getParticipants().add(memberId);
        chatRoomRepository.save(chatRoom);
        chatProfileRepository.appendChatRoom(memberId, chatRoomId);
    }

    public void exitChatRoom(String chatRoomId, Long memberId) {
        ChatRoom chatRoom = chatRoomRepository.findByIdWithOutMessages(chatRoomId);
        chatRoom.leave(memberId);
        // 채팅방에 유저가 한명도 남지 않은 상황이라면 채팅방 삭제
        if (chatRoom.getParticipants().isEmpty()) {
            chatRoomRepository.delete(chatRoomId);
        } else {
            chatRoomRepository.save(chatRoom);
        }

        chatProfileRepository.removeChatRoom(memberId, chatRoomId);
    }
}
