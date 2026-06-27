package me.splleat.messengerproject.application.profile;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.profile.dto.UserProfileDetailResult;
import me.splleat.messengerproject.common.annotation.UseCase;
import me.splleat.messengerproject.common.exception.BusinessException;
import me.splleat.messengerproject.common.exception.ErrorCode;
import me.splleat.messengerproject.infrastructure.persistence.querydsl.UserProfileQueryRepository;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
public class MyProfileGetUseCase {
    private final UserProfileQueryRepository userProfileQueryRepository;

    @Transactional(readOnly = true)
    public UserProfileDetailResult execute(long userId) {
        return userProfileQueryRepository.getMyProfile(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_PROFILE_NOT_FOUND));
    }
}
