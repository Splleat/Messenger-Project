package me.splleat.messengerproject.application.profile;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.profile.dto.UserProfileDetailResult;
import me.splleat.messengerproject.common.annotation.UseCase;
import me.splleat.messengerproject.domain.user.exception.UserProfileNotFoundException;
import me.splleat.messengerproject.infrastructure.persistence.querydsl.UserProfileQueryRepository;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
public class MyProfileGetUseCase {
    private final UserProfileQueryRepository userProfileQueryRepository;

    @Transactional(readOnly = true)
    public UserProfileDetailResult execute(long userId) {
        return userProfileQueryRepository.getMyProfile(userId)
                .orElseThrow(UserProfileNotFoundException::new);
    }
}
