package me.splleat.messengerproject.application.group;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.group.dto.GroupInviteCommand;
import me.splleat.messengerproject.common.annotation.UseCase;
import me.splleat.messengerproject.domain.member.GroupMember;
import me.splleat.messengerproject.domain.member.GroupMemberService;
import me.splleat.messengerproject.domain.member.GroupRole;
import me.splleat.messengerproject.domain.profile.UserProfile;
import me.splleat.messengerproject.domain.profile.UserProfileService;
import me.splleat.messengerproject.domain.user.UserService;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@UseCase
@RequiredArgsConstructor
public class GroupInviteUseCase {
    private final UserService userService;
    private final UserProfileService userProfileService;
    private final GroupMemberService groupMemberService;

    @Transactional
    public void execute(GroupInviteCommand command) {
        groupMemberService.validateParticipant(command.userId(), command.groupId());

        userService.validateExistsAll(command.targetIds());

        Set<Long> alreadyJoinedUserIds = new HashSet<>(groupMemberService.getAlreadyJoinedUserIds(command.groupId(), command.targetIds()));

        List<Long> targetIds = command.targetIds().stream()
                .filter(targetId -> !alreadyJoinedUserIds.contains(targetId))
                .toList();

        Map<Long, UserProfile> userProfileMap = userProfileService.getUserProfileMap(targetIds);

        List<GroupMember> newGroupMembers = targetIds.stream()
                .map(targetId -> {
                    UserProfile profile = userProfileMap.get(targetId);

                    String nickname = (profile != null) ? profile.getName() : "탈퇴한 사용자";

                    return GroupMember.create(targetId, command.groupId(), nickname, GroupRole.MEMBER);
                })
                .toList();

        groupMemberService.registerAll(newGroupMembers);
    }
}
