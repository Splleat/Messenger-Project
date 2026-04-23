package me.splleat.messengerproject.application.group;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.group.dto.GroupCreateCommand;
import me.splleat.messengerproject.common.annotation.UseCase;
import me.splleat.messengerproject.domain.group.Group;
import me.splleat.messengerproject.domain.group.GroupService;
import me.splleat.messengerproject.domain.member.GroupMember;
import me.splleat.messengerproject.domain.member.GroupMemberService;
import me.splleat.messengerproject.domain.member.GroupRole;
import me.splleat.messengerproject.domain.user.User;
import me.splleat.messengerproject.domain.user.UserService;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
public class GroupCreateUseCase {
    private final UserService userService;
    private final GroupService groupService;
    private final GroupMemberService groupMemberService;

    @Transactional
    public void execute(GroupCreateCommand command) {
        User user = userService.getUser(command.userId());

        Group group = Group.create(command.groupName());

        Group created = groupService.register(group);

        GroupMember groupMember = GroupMember.create(user, created, command.nickname(), GroupRole.OWNER);

        groupMemberService.register(groupMember);
    }
}
