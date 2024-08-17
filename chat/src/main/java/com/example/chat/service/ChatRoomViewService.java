package com.example.chat.service;

import com.example.chat.domain.chat.ChatRoom;
import com.example.chat.domain.chat.JoinedChatRoom;
import com.example.chat.domain.member.Profile;
import com.example.chat.dto.ChatRoomListInfoDto;
import com.example.chat.dto.response.ChatRoomListViewResponseDto;
import com.example.chat.repository.ChatProfileRepository;
import com.example.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomViewService {
    private final ChatProfileRepository chatProfileRepository;
    private final ChatRoomRepository chatRoomRepository;

    public ChatRoomListViewResponseDto getChatRoomList(Long memberId) {
        List<ChatRoomListInfoDto> chatRoomList = new ArrayList<>();

        Profile profile = chatProfileRepository.findById(memberId);

        for (JoinedChatRoom joinedChatRoom : profile.getJoinedChatRooms()) {
            String chatRoomId = joinedChatRoom.getChatRoomId();
            ChatRoom chatRoom = chatRoomRepository.findByIdWithRecentMessages(chatRoomId);
            Integer unreadMessageCount = countUnreadMessage(chatRoom.getRecentMessages(), joinedChatRoom.getLastAccessTime());
            chatRoomList.add(new ChatRoomListInfoDto(chatRoom, unreadMessageCount));
        }

        return new ChatRoomListViewResponseDto(chatRoomList);
    }

    private Integer countUnreadMessage(List<ChatRoom.Message> messages, LocalDateTime lastAccessTime) {
        int unreadCount = 0;
        for (ChatRoom.Message message : messages) {
            if (message.getSendTime().isAfter(lastAccessTime)) {
                unreadCount++;
            }
        }
        return unreadCount;
    }
}
