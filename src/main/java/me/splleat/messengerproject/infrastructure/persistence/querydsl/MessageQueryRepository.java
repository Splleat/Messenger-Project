package me.splleat.messengerproject.infrastructure.persistence.querydsl;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.domain.message.QMessage;
import me.splleat.messengerproject.domain.profile.QUserProfile;
import me.splleat.messengerproject.interfaces.websocket.message.dto.MessageCursorNextResponse;
import me.splleat.messengerproject.interfaces.websocket.message.dto.MessageCursorPrevResponse;
import me.splleat.messengerproject.interfaces.websocket.message.dto.MessagePageResponse;
import me.splleat.messengerproject.interfaces.websocket.message.dto.MessageResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MessageQueryRepository {
    private static final int MESSAGE_SIZE = 20;

    private final JPAQueryFactory queryFactory;

    public MessageCursorPrevResponse findByPrevId(long channelId, long cursorId) {
        Slice<MessageResponse> prev = findBy(channelId, QMessage.message.id.lt(cursorId), QMessage.message.id.desc(), true);

        return MessageCursorPrevResponse.from(prev);
    }

    public MessageCursorNextResponse findByNextId(long channelId, long cursorId) {
        Slice<MessageResponse> next = findBy(channelId, QMessage.message.id.gt(cursorId), QMessage.message.id.asc(), false);

        return MessageCursorNextResponse.from(next);
    }

    public MessagePageResponse findByNewest(long channelId) {
        Slice<MessageResponse> newest = findBy(channelId, null, QMessage.message.id.desc(), true);

        return MessagePageResponse.newest(newest);
    }

    public MessagePageResponse findByAroundId(long channelId, long cursorId) {
        Slice<MessageResponse> prev = findBy(channelId, QMessage.message.id.lt(cursorId), QMessage.message.id.desc(), true);
        Slice<MessageResponse> next = findBy(channelId, QMessage.message.id.goe(cursorId), QMessage.message.id.asc(), false);

        return MessagePageResponse.of(prev, next);
    }

    private Slice<MessageResponse> findBy(long channelId, Predicate predicate, OrderSpecifier<?> orderSpecifier, boolean reverse) {
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
                        predicate,
                        message.channelId.eq(channelId)
                )
                .orderBy(orderSpecifier)
                .limit((MESSAGE_SIZE + 1))
                .fetch();

        boolean hasMore = false;
        if (content.size() > MESSAGE_SIZE) {
            content.remove(MESSAGE_SIZE);
            hasMore = true;
        }

        List<MessageResponse> result = reverse ? content.reversed() : content;

        return new SliceImpl<>(result, PageRequest.of(0, MESSAGE_SIZE), hasMore);
    }
}
