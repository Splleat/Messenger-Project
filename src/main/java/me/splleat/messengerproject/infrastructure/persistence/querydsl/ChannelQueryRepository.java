package me.splleat.messengerproject.infrastructure.persistence.querydsl;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLSubQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.channel.dto.ChannelListResult;
import me.splleat.messengerproject.domain.channel.QChannel;
import me.splleat.messengerproject.domain.channel.QChannelUserSetting;
import me.splleat.messengerproject.domain.message.QMessage;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChannelQueryRepository {
    private final JPAQueryFactory queryFactory;

    public List<ChannelListResult> findAllGroupChannel(long userId, long groupId) {
        return findAllChannelList(userId, groupId);
    }

    public List<ChannelListResult> findAllDirectChannel(long userId) {
        return findAllChannelList(userId, null);
    }

    private List<ChannelListResult> findAllChannelList(long userId, Long groupId) {
        QChannel channel = QChannel.channel;
        QChannelUserSetting channelUserSetting = QChannelUserSetting.channelUserSetting;
        QMessage message = QMessage.message;

        JPQLSubQuery<Long> latestMessageId = JPAExpressions
                .select(message.id.max().coalesce(0L))
                .from(message)
                .where(message.channelId.eq(channel.id));

        BooleanExpression hasUnreadMessage = latestMessageId.gt(channelUserSetting.lastReadMessageId.coalesce(0L));

        return queryFactory
                .select(Projections.constructor(ChannelListResult.class,
                        channel.id,
                        channel.name,
                        hasUnreadMessage
                ))
                .from(channelUserSetting)
                .join(channel).on(channelUserSetting.channelId.eq(channel.id))
                .where(
                        channelUserSetting.userId.eq(userId),
                        groupId != null ? channel.groupId.eq(groupId) : channel.groupId.isNull()
                )
                .fetch();
    }
}