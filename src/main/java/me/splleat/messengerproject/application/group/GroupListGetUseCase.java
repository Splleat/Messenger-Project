package me.splleat.messengerproject.application.group;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.common.annotation.UseCase;
import me.splleat.messengerproject.domain.group.Group;
import me.splleat.messengerproject.domain.group.GroupService;
import me.splleat.messengerproject.domain.member.GroupMemberService;
import me.splleat.messengerproject.interfaces.rest.channel.dto.GroupListResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@UseCase
@RequiredArgsConstructor
public class GroupListGetUseCase {
    private final GroupService groupService;
    private final GroupMemberService groupMemberService;

    @Transactional(readOnly = true)
    public List<GroupListResponse> execute(long userId) {
        List<Long> joinedGroupIds = groupMemberService.getAllJoinedGroupIds(userId);

        List<Group> joinedGroups = groupService.getGroups(joinedGroupIds);

        return joinedGroups.stream()
                .map(GroupListResponse::from)
                .toList();
    }
}
