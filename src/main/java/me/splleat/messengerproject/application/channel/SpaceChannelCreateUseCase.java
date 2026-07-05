package me.splleat.messengerproject.application.channel;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.channel.dto.SpaceChannelCreateCommand;
import me.splleat.messengerproject.common.annotation.UseCase;
import me.splleat.messengerproject.domain.channel.Channel;
import me.splleat.messengerproject.domain.channel.ChannelService;
import me.splleat.messengerproject.domain.channel.ChannelUserSetting;
import me.splleat.messengerproject.domain.channel.ChannelUserSettingService;
import me.splleat.messengerproject.domain.space.SpaceMember;
import me.splleat.messengerproject.domain.space.SpaceMemberService;
import me.splleat.messengerproject.domain.space.SpaceRole;
import me.splleat.messengerproject.infrastructure.cache.ChannelCacheEvictor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@UseCase
@RequiredArgsConstructor
public class SpaceChannelCreateUseCase {
    private final SpaceMemberService spaceMemberService;
    private final ChannelService channelService;
    private final ChannelUserSettingService channelUserSettingService;
    private final ChannelCacheEvictor cacheEvictor;

    @Transactional
    public void execute(SpaceChannelCreateCommand command) {
        SpaceMember spaceMember = spaceMemberService.getSpaceMember(command.userId(), command.spaceId());

        spaceMember.validatePermission(SpaceRole.ADMIN);

        Channel createdChannel = channelService.register(Channel.createSpaceChannel(command.spaceId(), command.channelName(), command.type()));

        List<Long> spaceParticipantUserIds = spaceMemberService.getAllParticipantUserIds(command.spaceId());

        List<ChannelUserSetting> channelUserSettingList = spaceParticipantUserIds.stream()
                .map(userId -> ChannelUserSetting.create(userId, createdChannel.getId()))
                .toList();

        channelUserSettingService.registerAll(channelUserSettingList);

        spaceParticipantUserIds.forEach(userId -> cacheEvictor.evictSpaceChannels(userId, command.spaceId()));
    }
}
