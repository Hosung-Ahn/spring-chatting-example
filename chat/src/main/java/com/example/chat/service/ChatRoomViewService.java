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

    public ChatRoomViewResponseDto getChatRoom(Long memberId, String chatRoomId) {
        ChatRoom chatRoomWithRecentMessages = chatRoomRepository.findByIdWithRecentMessages(chatRoomId);
        Profile profile = chatProfileRepository.findById(memberId);
        LocalDateTime lastAccessTime = getLastAccessTime(profile.getJoinedChatRooms(), chatRoomId);

        String title = chatRoomWithRecentMessages.getTitle();
        Long ownerId = chatRoomWithRecentMessages.getOwnerId();
        List<Long> participants = chatRoomWithRecentMessages.getParticipants();

        List<ChatRoom.Message> messages = chatRoomWithRecentMessages.getRecentMessages();
        Integer unreadMessageCount = countUnreadMessage(messages, lastAccessTime);
        // 최신 메세지에서 읽지 않은 메세지를 모두 찾을 수 있으면 최신 메세지만 리턴
        if (unreadMessageCount < RECENT_MESSAGE_LIMIT) {

            return new ChatRoomViewResponseDto(
                    title, ownerId, participants,
                    chatRoomWithRecentMessages.getRecentMessages(), unreadMessageCount
            );
        // 읽지 않은 메세지가 최신 메세지를 넘어 백업 메세지까지 존재한다면 백업 메세지 중 읽지 않은 메세지를 리턴
        } else {
            ChatRoom chatRoomWithBackupMessages = chatRoomRepository.findByIdWithBackupMessages(chatRoomId);
            messages = chatRoomWithBackupMessages.getBackupMessages();
            unreadMessageCount = countUnreadMessage(messages, lastAccessTime);

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

    public ChatRoomListViewResponseDto getChatRoomList(Long memberId) {
        List<ChatRoomListInfoDto> chatRoomList = new ArrayList<>();

        Profile profile = chatProfileRepository.findById(memberId);

        for (JoinedChatRoom joinedChatRoom : profile.getJoinedChatRooms()) {
            String chatRoomId = joinedChatRoom.getChatRoomId();
            ChatRoom chatRoom = chatRoomRepository.findByIdWithRecentMessages(chatRoomId);
            Integer unreadMessageCount = countUnreadMessage(chatRoom.getRecentMessages(), joinedChatRoom.getLastAccessTime());
            chatRoomList.add(new ChatRoomListInfoDto(chatRoom, unreadMessageCount));
        }
        sortByLastMessageTime(chatRoomList);

        return new ChatRoomListViewResponseDto(chatRoomList);
    }

    private Integer countUnreadMessage(List<ChatRoom.Message> messages, LocalDateTime lastAccessTime) {
        // message 는 시간 순으로 정렬되어있다는 것을 이용해서 이진 탐색을 통해 읽지 않은 메세지의 개수를 찾습니다.
        int lo = 0;
        int hi = messages.size();

        while (lo <= hi) {
            int mid = (lo + hi)/2;

            if (messages.get(mid).getSendTime().isAfter(lastAccessTime)) {
                hi = mid-1;
            } else {
                lo = mid+1;
            }
        }
        return messages.size()-lo;
    }

    private void sortByLastMessageTime(List<ChatRoomListInfoDto> chatRoomList) {
        chatRoomList.sort(
                (o1, o2) -> o2.getLastMessage().getSendTime().compareTo(o1.getLastMessage().getSendTime())
        );
    }
}
