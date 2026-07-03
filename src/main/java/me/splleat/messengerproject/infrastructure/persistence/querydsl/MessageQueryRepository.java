package me.splleat.messengerproject.infrastructure.persistence.querydsl;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.channel.dto.ChannelEnterResult;
import me.splleat.messengerproject.application.channel.dto.ChannelMessagePageResult;
import me.splleat.messengerproject.common.util.StorageUrlMapper;
import me.splleat.messengerproject.domain.message.QAttachment;
import me.splleat.messengerproject.domain.message.QMessage;
import me.splleat.messengerproject.domain.user.QUserProfile;
import me.splleat.messengerproject.interfaces.websocket.message.dto.AttachmentResponse;
import me.splleat.messengerproject.interfaces.websocket.message.dto.MessageResponse;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MessageQueryRepository {
    private static final int MESSAGE_SIZE = 20;

    private final JPAQueryFactory queryFactory;
    private final StorageUrlMapper storageUrlMapper;

    public ChannelMessagePageResult findByPrevId(long channelId, long cursorId) {
        MessageSlice prev = findBy(channelId, QMessage.message.id.lt(cursorId), QMessage.message.id.desc(), true);

        return ChannelMessagePageResult.prev(prev);
    }

    public ChannelMessagePageResult findByNextId(long channelId, long cursorId) {
        MessageSlice next = findBy(channelId, QMessage.message.id.gt(cursorId), QMessage.message.id.asc(), false);

        return ChannelMessagePageResult.next(next);
    }

    public ChannelEnterResult findByNewest(long channelId) {
        MessageSlice newest = findBy(channelId, null, QMessage.message.id.desc(), true);

        return ChannelEnterResult.newest(newest);
    }

    public ChannelEnterResult findByAroundId(long channelId, long lastReadMessageId) {
        MessageSlice prev = findBy(channelId, QMessage.message.id.lt(lastReadMessageId), QMessage.message.id.desc(), true);
        MessageSlice next = findBy(channelId, QMessage.message.id.goe(lastReadMessageId), QMessage.message.id.asc(), false);

        return ChannelEnterResult.around(prev, next, lastReadMessageId);
    }

    private MessageSlice findBy(long channelId, Predicate predicate, OrderSpecifier<?> orderSpecifier, boolean reverse) {
        QUserProfile userProfile = QUserProfile.userProfile;
        QMessage message = QMessage.message;
        QAttachment attachment = QAttachment.attachment;

        List<MessageResponse> messages = queryFactory
                .select(Projections.constructor(MessageResponse.class,
                        message.id,
                        message.userId,
                        message.channelId,
                        userProfile.name,
                        userProfile.imageUrl,
                        message.content,
                        message.idemPotencyKey,
                        message.type,
                        message.parentMessageId,
                        message.createdAt
                ))
                .from(message)
                .leftJoin(userProfile).on(message.userId.eq(userProfile.userId))
                .where(
                        predicate,
                        message.channelId.eq(channelId)
                )
                .orderBy(orderSpecifier)
                .limit((MESSAGE_SIZE + 1))
                .fetch();

        boolean hasMore = false;
        if (messages.size() > MESSAGE_SIZE) {
            messages.remove(MESSAGE_SIZE);
            hasMore = true;
        }

        List<Long> messageIds = messages.stream()
                .map(MessageResponse::id)
                .toList();

        List<AttachmentResponse> attachments = queryFactory
                .select(Projections.constructor(AttachmentResponse.class,
                        attachment.id,
                        attachment.messageId,
                        attachment.originalName,
                        attachment.type,
                        attachment.url,
                        attachment.size))
                .from(attachment)
                .where(attachment.messageId.in(messageIds))
                .fetch();

        Map<Long, List<AttachmentResponse>> attachmentMap = attachments.stream()
                .collect(Collectors.groupingBy(AttachmentResponse::messageId));

        List<MessageResponse> result = messages.stream()
                .map(msg -> {
                    String fullProfileUrl = storageUrlMapper.resolve(msg.profileUrl());
                    
                    List<AttachmentResponse> msgAttachments = attachmentMap.getOrDefault(msg.id(), List.of()).stream()
                            .map(att -> new AttachmentResponse(
                                    att.id(),
                                    att.messageId(),
                                    att.name(),
                                    att.type(),
                                    storageUrlMapper.resolve(att.url()),
                                    att.size()))
                            .toList();

                    return new MessageResponse(
                            msg.id(),
                            msg.userId(),
                            msg.channelId(),
                            msg.username(),
                            fullProfileUrl,
                            msg.content(),
                            msg.idemPotencyKey(),
                            msg.type(),
                            msg.parentMessageId(),
                            msgAttachments,
                            msg.createdAt()
                    );
                })
                .toList();

        List<MessageResponse> finalResult = reverse ? result.reversed() : result;

        return new MessageSlice(finalResult, hasMore);
    }
}
