package me.splleat.messengerproject.application.group;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.group.dto.GroupResult;
import me.splleat.messengerproject.common.annotation.UseCase;
import me.splleat.messengerproject.domain.channel.Channel;
import me.splleat.messengerproject.domain.channel.ChannelService;
import me.splleat.messengerproject.domain.group.Group;
import me.splleat.messengerproject.domain.group.GroupService;
import me.splleat.messengerproject.domain.member.GroupMemberService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@UseCase
@RequiredArgsConstructor
public class GroupGetUseCase {
    private final GroupService groupService;
    private final GroupMemberService groupMemberService;
    private final ChannelService channelService;

    @Transactional(readOnly = true)
    public GroupResult execute(long userId, long groupId) {
        groupMemberService.validateParticipant(userId, groupId);

        Group group = groupService.getGroup(groupId);

        List<Channel> groupChannels = channelService.getGroupChannels(groupId);

        return GroupResult.of(group, groupChannels);
    }
}
