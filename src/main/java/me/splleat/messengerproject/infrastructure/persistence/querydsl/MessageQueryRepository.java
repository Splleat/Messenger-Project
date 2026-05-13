package me.splleat.messengerproject.infrastructure.persistence.querydsl;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.domain.message.QMessage;
import me.splleat.messengerproject.domain.profile.QUserProfile;
import me.splleat.messengerproject.interfaces.websocket.message.dto.MessageResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MessageQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Slice<MessageResponse> findByPrevId(long channelId, long cursorId, int reqSize) {
        QUserProfile userProfile = QUserProfile.userProfile;
        QMessage message = QMessage.message;

        List<MessageResponse> content = queryFactory
                .select(Projections.constructor(MessageResponse.class,
                        message.id,
                        message.userId,
                        message.channelId,
                        userProfile.name,
                        userProfile.imageUrl,
                        message.content,
                        message.type,
                        message.parentMessageId,
                        message.createdAt
                        ))
                .from(message)
                .leftJoin(userProfile).on(message.userId.eq(userProfile.id))
                .where(
                        message.id.lt(cursorId),
                        message.channelId.eq(channelId)
                )
                .orderBy(message.id.desc())
                .limit((reqSize + 1))
                .fetch();

        boolean hasPrev = false;
        if (content.size() > reqSize) {
            content.remove(reqSize);
            hasPrev = true;
        }

        return new SliceImpl<>(content.reversed(), PageRequest.of(0, reqSize), hasPrev);
    }

    public Slice<MessageResponse> findByNextId(long channelId, long cursorId, int reqSize) {
        QUserProfile userProfile = QUserProfile.userProfile;
        QMessage message = QMessage.message;

        List<MessageResponse> content = queryFactory
                .select(Projections.constructor(MessageResponse.class,
                        message.id,
                        message.userId,
                        message.channelId,
                        userProfile.name,
                        userProfile.imageUrl,
                        message.content,
                        message.type,
                        message.parentMessageId,
                        message.createdAt
                ))
                .from(message)
                .leftJoin(userProfile).on(message.userId.eq(userProfile.id))
                .where(
                        message.id.gt(cursorId),
                        message.channelId.eq(channelId)
                )
                .orderBy(message.id.asc())
                .limit((reqSize + 1))
                .fetch();

        boolean hasNext = false;
        if (content.size() > reqSize) {
            content.remove(reqSize);
            hasNext = true;
        }

        return new SliceImpl<>(content, PageRequest.of(0, reqSize), hasNext);
    }

    public Slice<MessageResponse> findByNewest(long channelId, int reqSize) {
        QUserProfile userProfile = QUserProfile.userProfile;
        QMessage message = QMessage.message;

        List<MessageResponse> content = queryFactory
                .select(Projections.constructor(MessageResponse.class,
                        message.id,
                        message.userId,
                        message.channelId,
                        userProfile.name,
                        userProfile.imageUrl,
                        message.content,
                        message.type,
                        message.parentMessageId,
                        message.createdAt
                ))
                .from(message)
                .leftJoin(userProfile).on(message.userId.eq(userProfile.id))
                .where(message.channelId.eq(channelId))
                .orderBy(message.id.desc())
                .limit((reqSize + 1))
                .fetch();

        boolean hasNext = false;
        if (content.size() > reqSize) {
            content.remove(reqSize);
            hasNext = true;
        }

        return new SliceImpl<>(content.reversed(), PageRequest.of(0, reqSize), hasNext);
    }
}
