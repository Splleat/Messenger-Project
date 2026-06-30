package me.splleat.messengerproject.application.profile;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.profile.dto.UserProfileResult;
import me.splleat.messengerproject.application.profile.dto.UserSearchCommand;
import me.splleat.messengerproject.common.annotation.UseCase;
import me.splleat.messengerproject.infrastructure.persistence.querydsl.UserProfileQueryRepository;

import java.util.List;

@UseCase
@RequiredArgsConstructor
public class UserSearchUseCase {
    private final UserProfileQueryRepository userProfileQueryRepository;

    public List<UserProfileResult> searchOtherUserProfiles(UserSearchCommand command) {
        return userProfileQueryRepository.searchOtherUserProfiles(command.excludeUserId(), command.name(), command.page());
    }
}
