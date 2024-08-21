package com.example.chat.repository;

import com.example.chat.domain.chat.JoinedChatRoom;
import com.example.chat.domain.member.Member;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.example.chat.domain.chat.QJoinedChatRoom.joinedChatRoom;
import static com.example.chat.domain.member.QMember.member;

@Repository
@RequiredArgsConstructor
@Transactional
public class JoinedChatRoomRepository {
    private final JPAQueryFactory query;

    public JoinedChatRoom findById(String chatRoomId, Long memberId) {
        Member findMember = query
                .selectFrom(member)
                .where(member.id.eq(memberId))
                .fetchOne();
        Long profileId = findMember.getProfile().getId();

        return query
                .selectFrom(joinedChatRoom)
                .where(joinedChatRoom.chatRoomId.eq(chatRoomId)
                        .and(joinedChatRoom.profile.id.eq(profileId)))
                .fetchOne();
    }
}
