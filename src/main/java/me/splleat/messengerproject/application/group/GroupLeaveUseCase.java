package me.splleat.messengerproject.application.group;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.group.dto.GroupLeaveCommand;
import me.splleat.messengerproject.common.annotation.UseCase;
import me.splleat.messengerproject.domain.channel.ChannelUserSettingService;
import me.splleat.messengerproject.domain.member.GroupMemberService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@UseCase
@RequiredArgsConstructor
public class GroupLeaveUseCase {
    private final GroupMemberService groupMemberService;
    private final ChannelUserSettingService channelUserSettingService;

    @Transactional
    public void execute(GroupLeaveCommand command) {
        groupMemberService.leaveGroup(command.userId(), command.groupId());

        List<Long> joinedGroupChannelIds = channelUserSettingService.getJoinedChannelIds(command.userId());

        channelUserSettingService.leaveChannels(command.userId(), joinedGroupChannelIds);
    }
}
