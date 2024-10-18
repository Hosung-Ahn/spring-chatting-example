package com.example.chat.service;

import com.example.chat.domain.chat.ChatRoom;
import com.example.chat.repository.ChatRoomRepository;
import com.example.chat.repository.UnreadCountCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CountMessageService {
    private final UnreadCountCacheRepository unreadCountCacheRepository;
    private final ChatRoomRepository chatRoomRepository;

    public Integer getUnreadCount(Long memberId, String chatRoomId, LocalDateTime lastAccessTime) {
        if (unreadCountCacheRepository.isUnreadCountExist(memberId, chatRoomId)) {
            String cachedRecentMessageId = unreadCountCacheRepository.getRecentMessageId(memberId, chatRoomId);
            Integer cachedUnreadCount = unreadCountCacheRepository.getUnreadCount(memberId, chatRoomId);

            ChatRoom chatRoom = chatRoomRepository.findByIdWithOutMessages(chatRoomId);
            String recentMessageId = chatRoom.getRecentMessage().getId();

            // 캐시된 마지막 message 와 채팅방의 마지막 메세지가 같다면 마지막 메세지 개수 확인 이후 메세지가 오지 않았다는 것을 의미합니다
            // 따라서 기존의 저장된 읽지 않은 메세지 개수를 그대로 반환합니다.
            if (cachedRecentMessageId.equals(recentMessageId)) {
                return cachedUnreadCount;
            }
            else if (cachedUnreadCount >= 100) {
                // 새로운 메세지가 도착했더라도 읽지 않은 메세지의 개수가 100개 이상이면 그대로 반환합니다.
                // 100개 이상 부터는 100+ 라고 표기하기 때문입니다.
                return cachedUnreadCount;
            }
        }
        // 캐시된 데이터가 없거나, 있더라도 새로운 메세지가 도착해서 읽지 않은 메세지 개수를 다시 계산해야 하는 경우
        ChatRoom chatRoomWithRecentMessages = chatRoomRepository.findByIdWithRecentMessages(chatRoomId);
        List<ChatRoom.Message> recentMessages = chatRoomWithRecentMessages.getMessages();
        return countUnreadMessage(recentMessages, lastAccessTime);
    }

    public Integer countUnreadMessage(List<ChatRoom.Message> messages, LocalDateTime lastAccessTime) {
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
}
