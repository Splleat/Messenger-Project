package me.splleat.messengerproject.infrastructure.persistence.querydsl;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.space.dto.SpaceListResult;
import me.splleat.messengerproject.domain.space.QSpace;
import me.splleat.messengerproject.domain.space.QSpaceMember;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SpaceQueryRepository {
    private final JPAQueryFactory queryFactory;

    public List<SpaceListResult> findSpaceList(long userId) {
        QSpace space = QSpace.space;
        QSpaceMember member = QSpaceMember.spaceMember;

        return queryFactory
                .select(Projections.constructor(SpaceListResult.class,
                        space.id,
                        space.name))
                .from(space)
                .join(member).on(space.id.eq(member.spaceId))
                .where(member.userId.eq(userId))
                .fetch();
    }
}
