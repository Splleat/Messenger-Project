package me.splleat.messengerproject.infrastructure.persistence.querydsl;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
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
        return findBy(channelId, reqSize, QMessage.message.id.lt(cursorId), QMessage.message.id.desc(), true);
    }

    public Slice<MessageResponse> findByNextId(long channelId, long cursorId, int reqSize) {
        return findBy(channelId, reqSize, QMessage.message.id.gt(cursorId), QMessage.message.id.asc(), false);
    }

    public Slice<MessageResponse> findByNewest(long channelId, int reqSize) {
        return findBy(channelId, reqSize, null, QMessage.message.id.desc(), true);
    }

    private Slice<MessageResponse> findBy(long channelId, int reqSize, Predicate predicate, OrderSpecifier<?> orderSpecifier, boolean reverse) {
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
                .limit((reqSize + 1))
                .fetch();

        boolean hasMore = false;
        if (content.size() > reqSize) {
            content.remove(reqSize);
            hasMore = true;
        }

        List<MessageResponse> result = reverse ? content.reversed() : content;

        return new SliceImpl<>(result, PageRequest.of(0, reqSize), hasMore);
    }
}
