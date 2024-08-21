package com.example.chat.domain.chat;

import com.example.chat.domain.member.Profile;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JoinedChatRoom {
    @Id @GeneratedValue
    @Column(name = "joined_chat_room_id")
    private Long id;

    private String chatRoomId;
    private LocalDateTime lastAccessTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    Profile profile;

    public JoinedChatRoom(String chatRoomId, Profile profile) {
        this.chatRoomId = chatRoomId;
        this.lastAccessTime = LocalDateTime.now();
        this.profile = profile;
    }

    public void updateLastAccessTime() {
        this.lastAccessTime = LocalDateTime.now();
    }
}
