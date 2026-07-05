package me.splleat.messengerproject.application.space;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.space.dto.SpaceCreateCommand;
import me.splleat.messengerproject.common.annotation.UseCase;
import me.splleat.messengerproject.domain.space.*;
import me.splleat.messengerproject.domain.user.UserProfile;
import me.splleat.messengerproject.domain.user.UserProfileService;
import me.splleat.messengerproject.infrastructure.cache.SpaceCacheEvictor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
public class SpaceCreateUseCase {
    private final UserProfileService userProfileService;
    private final SpaceService spaceService;
    private final SpaceMemberService spaceMemberService;
    private final SpaceCacheEvictor cacheEvictor;

    @Transactional
    public void execute(SpaceCreateCommand command) {
        Space createdSpace = spaceService.register(Space.create(command.spaceName()));

        UserProfile userProfile = userProfileService.getUserProfile(command.userId());

        SpaceMember spaceMember = SpaceMember.create(command.userId(), createdSpace.getId(), userProfile.getName(), SpaceRole.OWNER);

        spaceMemberService.register(spaceMember);

        cacheEvictor.evictSpaceList(command.userId());
    }
}
