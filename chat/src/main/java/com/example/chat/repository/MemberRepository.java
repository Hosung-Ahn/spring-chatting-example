package com.example.chat.repository;

import com.example.chat.domain.member.Member;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.example.chat.domain.member.QMember.member;

@Repository
@Transactional
@RequiredArgsConstructor
public class MemberRepository {
    private final JPAQueryFactory query;

    public Member findById(Long memberId) {
        return query
                .selectFrom(member)
                .where(member.id.eq(memberId))
                .fetchOne();
    }
}
