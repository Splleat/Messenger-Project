package me.splleat.messengerproject.application.group;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.group.dto.GroupEnterCommand;
import me.splleat.messengerproject.common.annotation.UseCase;
import me.splleat.messengerproject.domain.channel.ChannelService;
import me.splleat.messengerproject.domain.channel.ChannelUserSetting;
import me.splleat.messengerproject.domain.channel.ChannelUserSettingService;
import me.splleat.messengerproject.domain.member.GroupMember;
import me.splleat.messengerproject.domain.member.GroupMemberService;
import me.splleat.messengerproject.domain.member.GroupRole;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@UseCase
@RequiredArgsConstructor
public class GroupEnterUseCase {
    private final ChannelService channelService;
    private final ChannelUserSettingService channelUserSettingService;
    private final GroupMemberService groupMemberService;

    @Transactional
    public void execute(GroupEnterCommand command) {
        GroupMember groupMember = GroupMember.create(command.userId(), command.groupId(), command.nickname(), GroupRole.MEMBER);

        groupMemberService.register(groupMember);

        List<Long> groupChannelIds = channelService.getGroupChannelIds(command.groupId());

        List<ChannelUserSetting> newSettingsFromGroupChannels = groupChannelIds.stream()
                .map(channelId -> ChannelUserSetting.create(command.userId(), channelId))
                .toList();

        channelUserSettingService.registerAll(newSettingsFromGroupChannels);
    }
}
