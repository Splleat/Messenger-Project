package me.splleat.messengerproject.application.group;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.group.dto.GroupGetCommand;
import me.splleat.messengerproject.common.annotation.UseCase;
import me.splleat.messengerproject.domain.channel.Channel;
import me.splleat.messengerproject.domain.channel.ChannelService;
import me.splleat.messengerproject.domain.group.Group;
import me.splleat.messengerproject.domain.group.GroupService;
import me.splleat.messengerproject.domain.member.GroupMemberService;
import me.splleat.messengerproject.interfaces.rest.group.dto.GroupResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@UseCase
@RequiredArgsConstructor
public class GroupGetUseCase {
    private final GroupService groupService;
    private final GroupMemberService groupMemberService;
    private final ChannelService channelService;

    @Transactional(readOnly = true)
    public GroupResponse execute(GroupGetCommand command) {
        groupMemberService.validateParticipant(command.userId(), command.groupId());

        Group group = groupService.getGroup(command.groupId());

        List<Channel> groupChannels = channelService.getGroupChannels(command.groupId());

        return GroupResponse.of(group, groupChannels);
    }
}
