package me.splleat.messengerproject.infrastructure.persistence.querydsl;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.group.dto.GroupListResult;
import me.splleat.messengerproject.domain.group.QGroup;
import me.splleat.messengerproject.domain.member.QGroupMember;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class GroupQueryRepository {
    private final JPAQueryFactory queryFactory;

    public List<GroupListResult> findGroupList(long userId) {
        QGroup group = QGroup.group;
        QGroupMember member = QGroupMember.groupMember;

        return queryFactory
                .select(Projections.constructor(GroupListResult.class,
                        group.id,
                        group.name))
                .from(group)
                .join(member).on(group.id.eq(member.groupId))
                .where(member.userId.eq(userId))
                .fetch();
    }
}
