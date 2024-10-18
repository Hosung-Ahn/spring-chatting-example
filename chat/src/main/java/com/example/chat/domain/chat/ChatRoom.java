package com.example.chat.domain.chat;

import com.example.chat.dto.websocket.ChatMessageReceiveDto;
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
    private Long ownerId;
    private List<Long> participants = new ArrayList<>();
    private List<Message> messages = new ArrayList<>();

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

        public Message(ChatMessageReceiveDto chatDto) {
            this.senderId = chatDto.getSenderId();
            this.content = chatDto.getContent();
        }
    }

    public ChatRoom(String title, Long ownerId, List<Long> participants) {
        this.title = title;
        this.ownerId = ownerId;
        this.participants = participants;
    }

    public void leave(Long memberId) {
        participants.remove(memberId);
    }
}
