package com.example.chat.domain.chat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom {
    @Id
    private String id;
    @CreatedDate
    private LocalDateTime createdAt;

    private String title;
    private String ownerId;
    private List<Long> participants = new ArrayList<>();
    private List<Message> recentMessages = new ArrayList<>();
    private List<Message> backupMessages = new ArrayList<>();

    @Getter
    @NoArgsConstructor
    public static class Message {
        private Long senderId;
        private LocalDateTime sendTime;
        private String content;

        public Message(Long senderId, String content) {
            this.senderId = senderId;
            this.content = content;
            this.sendTime = LocalDateTime.now();
        }
    }

    public ChatRoom(String title, String ownerId, List<Long> participants) {
        this.title = title;
        this.ownerId = ownerId;
        this.participants = participants;
    }
}
