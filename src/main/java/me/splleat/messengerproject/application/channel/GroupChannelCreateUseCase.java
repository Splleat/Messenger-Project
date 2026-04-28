package me.splleat.messengerproject.application.channel;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.channel.dto.GroupChannelCreateCommand;
import me.splleat.messengerproject.common.annotation.UseCase;
import me.splleat.messengerproject.domain.channel.Channel;
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
public class GroupChannelCreateUseCase {
    private final GroupMemberService groupMemberService;
    private final ChannelService channelService;
    private final ChannelUserSettingService channelUserSettingService;

    @Transactional
    public void execute(GroupChannelCreateCommand command) {
        GroupMember groupMember = groupMemberService.getGroupMember(command.userId(), command.groupId());
        groupMember.validatePermission(GroupRole.ADMIN);

        Channel channel = Channel.create(command.groupId(), command.channelName(), command.type());
        Channel created = channelService.register(channel);

        List<Long> groupMemberIdList = groupMemberService.getAllGroupMemberUserId(command.groupId());

        List<ChannelUserSetting> channelUserSettingList = groupMemberIdList.stream()
                .map(userId -> ChannelUserSetting.create(userId, created))
                .toList();

        channelUserSettingService.registerAll(channelUserSettingList);
    }
}
