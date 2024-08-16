package com.example.chat.domain.member;

import com.example.chat.domain.chat.JoinedChatRoom;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Profile {
    @Id @GeneratedValue
    @Column(name = "profile_id")
    private Long id;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JoinedChatRoom> joinedChatRooms;

    public void appendChatRoom(String chatRoomId) {
        joinedChatRooms.add(new JoinedChatRoom(chatRoomId, this));
    }

    public void removeChatRoom(String chatRoomId) {
        joinedChatRooms.removeIf(joinedChatRoom -> joinedChatRoom.getChatRoomId().equals(chatRoomId));
    }
}
