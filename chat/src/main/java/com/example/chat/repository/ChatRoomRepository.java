package com.example.chat.repository;

import com.example.chat.domain.chat.ChatRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChatRoomRepository {
    private final MongoTemplate mongoTemplate;
    private static final Integer RECENT_MESSAGE_LIMIT = 100;
    private static final Integer BACKUP_MESSAGE_LIMIT = 10000;

    public String save(ChatRoom chatRoom) {
        mongoTemplate.save(chatRoom);
        return chatRoom.getId();
    }

    public ChatRoom findById(String chatRoomId) {
        return mongoTemplate.findById(chatRoomId, ChatRoom.class);
    }

    public void deleteById(String chatRoomId) {
        Query query = new Query(Criteria.where("_id").is(chatRoomId));
        mongoTemplate.remove(query, ChatRoom.class);
    }
}
