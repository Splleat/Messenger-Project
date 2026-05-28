package me.splleat.messengerproject.infrastructure.persistence.querydsl;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.JPQLSubQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.channel.dto.ChannelListResult;
import me.splleat.messengerproject.domain.channel.QChannel;
import me.splleat.messengerproject.domain.channel.QChannelUserSetting;
import me.splleat.messengerproject.domain.member.QGroupMember;
import me.splleat.messengerproject.domain.message.QMessage;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChannelQueryRepository {
    private final JPAQueryFactory queryFactory;

    public List<ChannelListResult> findDirectChannelList(long userId) {
        QChannel channel = QChannel.channel;
        QChannelUserSetting setting = QChannelUserSetting.channelUserSetting;

        JPQLQuery<?> query = queryFactory
                .from(setting)
                .join(channel).on(setting.channelId.eq(channel.id))
                .where(
                        setting.userId.eq(userId),
                        channel.groupId.isNull()
                );

        return findChannelList(query);
    }

    public List<ChannelListResult> findGroupChannelList(long userId, long groupId) {
        QChannel channel = QChannel.channel;
        QChannelUserSetting setting = QChannelUserSetting.channelUserSetting;
        QGroupMember member = QGroupMember.groupMember;

        JPQLQuery<?> query = queryFactory
                .from(channel)
                .leftJoin(setting).on(channel.id.eq(setting.channelId)
                        .and(setting.userId.eq(userId)))
                .join(member).on(member.groupId.eq(channel.groupId))
                .where(
                        member.groupId.eq(groupId),
                        member.userId.eq(userId)
                );

        return findChannelList(query);
    }

    private List<ChannelListResult> findChannelList(JPQLQuery<?> query) {
        QChannel channel = QChannel.channel;
        QChannelUserSetting setting = QChannelUserSetting.channelUserSetting;
        QMessage message = QMessage.message;

        JPQLSubQuery<Long> latestMessageId = JPAExpressions
                .select(message.id.max().coalesce(0L))
                .from(message)
                .where(message.channelId.eq(channel.id));

        NumberExpression<Long> lastReadMessageId = setting.lastReadMessageId.coalesce(0L);
        BooleanExpression hasUnreadMessage = latestMessageId.gt(lastReadMessageId);

        return query
                .select(Projections.constructor(ChannelListResult.class,
                        channel.id,
                        channel.name,
                        hasUnreadMessage))
                .fetch();
    }
}
