package me.splleat.messengerproject.application.space;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.space.dto.SpaceInviteCommand;
import me.splleat.messengerproject.common.annotation.UseCase;
import me.splleat.messengerproject.domain.space.SpaceMember;
import me.splleat.messengerproject.domain.space.SpaceMemberService;
import me.splleat.messengerproject.domain.space.SpaceRole;
import me.splleat.messengerproject.domain.user.UserProfile;
import me.splleat.messengerproject.domain.user.UserProfileService;
import me.splleat.messengerproject.domain.user.UserService;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@UseCase
@RequiredArgsConstructor
public class SpaceInviteUseCase {
    private final UserService userService;
    private final UserProfileService userProfileService;
    private final SpaceMemberService spaceMemberService;

    @Transactional
    public void execute(SpaceInviteCommand command) {
        spaceMemberService.validateParticipant(command.userId(), command.spaceId());

        userService.validateExistsAll(command.targetIds());

        Set<Long> alreadyJoinedUserIds = new HashSet<>(spaceMemberService.getAlreadyJoinedUserIds(command.spaceId(), command.targetIds()));

        List<Long> targetIds = command.targetIds().stream()
                .filter(targetId -> !alreadyJoinedUserIds.contains(targetId))
                .toList();

        Map<Long, UserProfile> userProfileMap = userProfileService.getUserProfileMap(targetIds);

        List<SpaceMember> newSpaceMembers = targetIds.stream()
                .map(targetId -> {
                    UserProfile profile = userProfileMap.get(targetId);

                    String nickname = (profile != null) ? profile.getName() : "탈퇴한 사용자";

                    return SpaceMember.create(targetId, command.spaceId(), nickname, SpaceRole.MEMBER);
                })
                .toList();

        spaceMemberService.registerAll(newSpaceMembers);
    }
}
