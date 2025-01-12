package com.example.chat.service;

import com.example.chat.domain.chat.ChatRoom;
import com.example.chat.domain.chat.JoinedChatRoom;
import com.example.chat.domain.member.Profile;
import com.example.chat.dto.ChatRoomListInfoDto;
import com.example.chat.dto.response.ChatRoomListViewResponseDto;
import com.example.chat.dto.response.ChatRoomViewResponseDto;
import com.example.chat.repository.ChatProfileRepository;
import com.example.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ChatRoomViewService {
    private static final Integer RECENT_MESSAGE_LIMIT = 100;
    private static final Integer BACKUP_MESSAGE_LIMIT = 10000;

    private final ChatProfileRepository chatProfileRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final CountMessageService countMessageService;

    public ChatRoomViewResponseDto getChatRoom(Long memberId, String chatRoomId) {
        ChatRoom chatRoomWithRecentMessages = chatRoomRepository.findByIdWithRecentMessages(chatRoomId);
        Profile profile = chatProfileRepository.findById(memberId);
        LocalDateTime lastAccessTime = getLastAccessTime(profile.getJoinedChatRooms(), chatRoomId);

        String title = chatRoomWithRecentMessages.getTitle();
        Long ownerId = chatRoomWithRecentMessages.getOwnerId();
        List<Long> participants = chatRoomWithRecentMessages.getParticipants();

        List<ChatRoom.Message> messages = chatRoomWithRecentMessages.getMessages();
        Integer unreadMessageCount = countMessageService.countUnreadMessage(messages, lastAccessTime);
        // 최신 메세지에서 읽지 않은 메세지를 모두 찾을 수 있으면 최신 메세지만 리턴
        if (unreadMessageCount < RECENT_MESSAGE_LIMIT) {

            return new ChatRoomViewResponseDto(
                    title, ownerId, participants,
                    chatRoomWithRecentMessages.getMessages(), unreadMessageCount
            );
        // 읽지 않은 메세지가 최신 메세지를 넘어 백업 메세지까지 존재한다면 백업 메세지 중 읽지 않은 메세지를 리턴
        } else {
            ChatRoom chatRoomWithBackupMessages = chatRoomRepository.findByIdWithBackupMessages(chatRoomId);
            messages = chatRoomWithBackupMessages.getMessages();
            unreadMessageCount = countMessageService.countUnreadMessage(messages, lastAccessTime);

            List<ChatRoom.Message> unreadMessages = getUnreadMessages(messages, unreadMessageCount);

            return new ChatRoomViewResponseDto(
                    title, ownerId, participants,
                    unreadMessages, unreadMessageCount
            );
        }
    }

    private List<ChatRoom.Message> getUnreadMessages(List<ChatRoom.Message> messages, Integer unreadMessageCount) {
        int fromIdx = messages.size()- unreadMessageCount;
        int toIdx = messages.size();
        List<ChatRoom.Message> unreadMessages = messages.subList(fromIdx, toIdx);
        return unreadMessages;
    }

    private LocalDateTime getLastAccessTime(List<JoinedChatRoom> chatRooms, String chatRoomId) {
        for (JoinedChatRoom chatRoom : chatRooms) {
            if (Objects.equals(chatRoom.getChatRoomId(), chatRoomId)) {
                return chatRoom.getLastAccessTime();
            }
        }
        throw new IllegalArgumentException("유저가 채팅방에 참여한 기록이 없습니다.");
    }



}
