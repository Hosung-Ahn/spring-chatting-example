package com.example.chat.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UnreadCountCacheRepository {
    private final RedisTemplate<String, String> redisTemplate;

    public void saveUnreadCount(Long memberId, String chatRoomId, String recentMessageId, Long unreadCount) {
        String key = getKey(memberId, chatRoomId);
        redisTemplate.opsForHash().put(key, "recentMessageId", recentMessageId);
        redisTemplate.opsForHash().put(key, "unreadCount", unreadCount.toString());
    }

    public boolean isUnreadCountExist(Long memberId, String chatRoomId) {
        String key = getKey(memberId, chatRoomId);
        return redisTemplate.opsForHash().hasKey(key, "unreadCount")
                && redisTemplate.opsForHash().hasKey(key, "recentMessageId");
    }

    public String getRecentMessageId(Long memberId, String chatRoomId) {
        String key = getKey(memberId, chatRoomId);
        return (String) redisTemplate.opsForHash().get(key, "recentMessageId");
    }

    public Integer getUnreadCount(Long memberId, String chatRoomId) {
        String key = getKey(memberId, chatRoomId);
        String unreadCount = (String) redisTemplate.opsForHash().get(key, "unreadCount");
        return unreadCount == null ? 0 : Integer.parseInt(unreadCount);
    }

    private String getKey(Long memberId, String chatRoomId) {
        return memberId + ":" + chatRoomId;
    }

}
