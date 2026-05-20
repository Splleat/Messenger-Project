package me.splleat.messengerproject.infrastructure.persistence.querydsl;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.domain.message.QMessage;
import me.splleat.messengerproject.domain.profile.QUserProfile;
import me.splleat.messengerproject.interfaces.rest.channel.dto.ChannelEnterResponse;
import me.splleat.messengerproject.interfaces.rest.channel.dto.ChannelMessagePageResponse;
import me.splleat.messengerproject.interfaces.websocket.message.dto.MessageResponse;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MessageQueryRepository {
    private static final int MESSAGE_SIZE = 20;

    private final JPAQueryFactory queryFactory;

    public ChannelMessagePageResponse findByPrevId(long channelId, long cursorId) {
        MessageSlice prev = findBy(channelId, QMessage.message.id.lt(cursorId), QMessage.message.id.desc(), true);

        return ChannelMessagePageResponse.prev(prev);
    }

    public ChannelMessagePageResponse findByNextId(long channelId, long cursorId) {
        MessageSlice next = findBy(channelId, QMessage.message.id.gt(cursorId), QMessage.message.id.asc(), false);

        return ChannelMessagePageResponse.next(next);
    }

    public ChannelEnterResponse findByNewest(long channelId) {
        MessageSlice newest = findBy(channelId, null, QMessage.message.id.desc(), true);

        return ChannelEnterResponse.newest(newest);
    }

    public ChannelEnterResponse findByAroundId(long channelId, long lastReadMessageId) {
        MessageSlice prev = findBy(channelId, QMessage.message.id.lt(lastReadMessageId), QMessage.message.id.desc(), true);
        MessageSlice next = findBy(channelId, QMessage.message.id.goe(lastReadMessageId), QMessage.message.id.asc(), false);

        return ChannelEnterResponse.around(prev, next, lastReadMessageId);
    }

    private MessageSlice findBy(long channelId, Predicate predicate, OrderSpecifier<?> orderSpecifier, boolean reverse) {
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

        return new MessageSlice(result, hasMore);
    }
}
