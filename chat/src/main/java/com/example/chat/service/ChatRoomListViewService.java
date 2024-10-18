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
public class ChatRoomListViewService {
    private final ChatProfileRepository chatProfileRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final CountMessageService countMessageService;

    public ChatRoomListViewResponseDto getChatRoomList(Long memberId) {
        List<ChatRoomListInfoDto> chatRoomList = new ArrayList<>();

        Profile profile = chatProfileRepository.findById(memberId);

        for (JoinedChatRoom joinedChatRoom : profile.getJoinedChatRooms()) {
            String chatRoomId = joinedChatRoom.getChatRoomId();
            ChatRoom chatRoom = chatRoomRepository.findByIdWithOutMessages(chatRoomId);
            LocalDateTime lastAccessTime = joinedChatRoom.getLastAccessTime();
            Integer unreadMessageCount = countMessageService.getUnreadCount(memberId, chatRoomId, lastAccessTime);
            chatRoomList.add(new ChatRoomListInfoDto(chatRoom, unreadMessageCount));
        }
        sortByLastMessageTime(chatRoomList);

        return new ChatRoomListViewResponseDto(chatRoomList);
    }

    private void sortByLastMessageTime(List<ChatRoomListInfoDto> chatRoomList) {
        chatRoomList.sort(
                (o1, o2) -> o2.getLastMessage().getSendTime().compareTo(o1.getLastMessage().getSendTime())
        );
    }
}
