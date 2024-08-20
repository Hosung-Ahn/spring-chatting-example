package com.example.chat.repository;

import com.example.chat.domain.chat.ChatRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
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

    public ChatRoom findByIdWithRecentMessages(String chatRoomId) {
        Query query = new Query(Criteria.where("_id").is(chatRoomId));
        query.fields().exclude("backupMessages");
        return mongoTemplate.findOne(query, ChatRoom.class);
    }

    public ChatRoom findByIdWithBackupMessages(String chatRoomId) {
        return mongoTemplate.findById(chatRoomId, ChatRoom.class);
    }

    public ChatRoom findByIdWithOutMessages(String chatRoomId) {
        Query query = new Query(Criteria.where("_id").is(chatRoomId));
        query.fields().exclude("recentMessages", "backupMessages");
        return mongoTemplate.findOne(query, ChatRoom.class);
    }

    public void deleteById(String chatRoomId) {
        Query query = new Query(Criteria.where("_id").is(chatRoomId));
        mongoTemplate.remove(query, ChatRoom.class);
    }

    public void delete(String chatRoomId) {
        Query query = new Query(Criteria.where("_id").is(chatRoomId));
        Update update = new Update().set("deleted", true);
        mongoTemplate.updateFirst(query, update, ChatRoom.class);
    }

    public void appendMessage(String chatRoomId, ChatRoom.Message message) {
        appendRecentMessage(chatRoomId, message);
        appendBackupMessage(chatRoomId, message);
    }

    private void appendRecentMessage(String chatRoomId, ChatRoom.Message message) {
        Query query = new Query(Criteria.where("_id").is(chatRoomId));
        Update update = new Update()
                .push("recentMessages")
                .slice(-RECENT_MESSAGE_LIMIT)
                .each(message);
        mongoTemplate.upsert(query, update, ChatRoom.class);
    }

    private void appendBackupMessage(String chatRoomId, ChatRoom.Message message) {
        Query query = new Query(Criteria.where("_id").is(chatRoomId));
        Update update = new Update()
                .push("backupMessages")
                .slice(-BACKUP_MESSAGE_LIMIT)
                .each(message);
        mongoTemplate.upsert(query, update, ChatRoom.class);
    }

}
