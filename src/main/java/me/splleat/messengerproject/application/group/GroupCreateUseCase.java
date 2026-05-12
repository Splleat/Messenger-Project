package me.splleat.messengerproject.application.group;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.group.dto.GroupCreateCommand;
import me.splleat.messengerproject.common.annotation.UseCase;
import me.splleat.messengerproject.domain.group.Group;
import me.splleat.messengerproject.domain.group.GroupService;
import me.splleat.messengerproject.domain.member.GroupMember;
import me.splleat.messengerproject.domain.member.GroupMemberService;
import me.splleat.messengerproject.domain.member.GroupRole;
import me.splleat.messengerproject.domain.profile.UserProfile;
import me.splleat.messengerproject.domain.profile.UserProfileService;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
public class GroupCreateUseCase {
    private final UserProfileService userProfileService;
    private final GroupService groupService;
    private final GroupMemberService groupMemberService;

    @Transactional
    public void execute(GroupCreateCommand command) {
        Group createdGroup = groupService.register(Group.create(command.groupName()));

        UserProfile userProfile = userProfileService.getUserProfile(command.userId());

        GroupMember groupMember = GroupMember.create(command.userId(), createdGroup.getId(), userProfile.getName(), GroupRole.OWNER);

        groupMemberService.register(groupMember);
    }
}
