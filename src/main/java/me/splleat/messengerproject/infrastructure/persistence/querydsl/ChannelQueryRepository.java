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
import me.splleat.messengerproject.application.channel.dto.ChannelParticipantResult;
import me.splleat.messengerproject.domain.channel.QChannel;
import me.splleat.messengerproject.domain.channel.QChannelUserSetting;
import me.splleat.messengerproject.domain.message.QMessage;
import me.splleat.messengerproject.domain.space.QSpaceMember;
import me.splleat.messengerproject.domain.user.QUserProfile;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChannelQueryRepository {
    private final JPAQueryFactory queryFactory;

    public List<ChannelParticipantResult> findChannelParticipant(long channelId) {
        QChannelUserSetting setting = QChannelUserSetting.channelUserSetting;
        QUserProfile profile = QUserProfile.userProfile;

        return queryFactory
                .select(Projections.constructor(ChannelParticipantResult.class,
                        profile.userId,
                        profile.name,
                        profile.imageUrl,
                        profile.statusMessage))
                .from(setting)
                .join(profile).on(setting.userId.eq(profile.userId))
                .where(setting.channelId.eq(channelId))
                .fetch();
    }

    @Cacheable(value = "directChannelList", key = "#userId")
    public List<ChannelListResult> findDirectChannelList(long userId) {
        QChannel channel = QChannel.channel;
        QChannelUserSetting setting = QChannelUserSetting.channelUserSetting;

        JPQLQuery<?> query = queryFactory
                .from(setting)
                .join(channel).on(setting.channelId.eq(channel.id))
                .where(
                        setting.userId.eq(userId),
                        channel.spaceId.isNull()
                );

        return findChannelList(query);
    }

    @Cacheable(value = "spaceChannelList", key = "#userId + '-' + #spaceId")
    public List<ChannelListResult> findSpaceChannelList(long userId, long spaceId) {
        QChannel channel = QChannel.channel;
        QChannelUserSetting setting = QChannelUserSetting.channelUserSetting;
        QSpaceMember member = QSpaceMember.spaceMember;

        JPQLQuery<?> query = queryFactory
                .from(channel)
                .leftJoin(setting).on(channel.id.eq(setting.channelId)
                        .and(setting.userId.eq(userId)))
                .join(member).on(member.spaceId.eq(channel.spaceId))
                .where(
                        member.spaceId.eq(spaceId),
                        member.userId.eq(userId)
                );

        return findChannelList(query);
    }

    public List<NotificationRecipientResult> findNotificationRecipients(long channelId, long excludeUserId) {
        QChannelUserSetting setting = QChannelUserSetting.channelUserSetting;

        return queryFactory
                .select(Projections.constructor(NotificationRecipientResult.class,
                        setting.userId,
                        setting.isMuted))
                .from(setting)
                .where(
                        setting.channelId.eq(channelId),
                        setting.userId.ne(excludeUserId)
                )
                .fetch();
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
