package com.example.chat.repository;

import com.example.chat.domain.member.Profile;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.example.chat.domain.member.QProfile.profile;

@Repository
@RequiredArgsConstructor
@Transactional
public class ChatProfileRepository {
    private final JPAQueryFactory query;

    public void appendChatRoom(Long memberId, String chatRoomId) {
        Profile profile = findById(memberId);
        profile.appendChatRoom(chatRoomId);
    }

    public void removeChatRoom(Long memberId, String chatRoomId) {
        Profile profile = findById(memberId);
        profile.removeChatRoom(chatRoomId);
    }

    private Profile findById(Long id) {
        return query
                .selectFrom(profile)
                .where(profile.id.eq(id))
                .fetchOne();

    }
}
