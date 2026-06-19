package me.splleat.messengerproject.application.profile;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.profile.dto.UserProfileResult;
import me.splleat.messengerproject.common.annotation.UseCase;
import me.splleat.messengerproject.common.util.StorageUrlMapper;
import me.splleat.messengerproject.domain.user.UserProfile;
import me.splleat.messengerproject.domain.user.UserProfileService;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
public class TargetProfileGetUseCase {
    private final UserProfileService userProfileService;
    private final StorageUrlMapper storageUrlMapper;

    @Transactional(readOnly = true)
    public UserProfileResult execute(long userId) {
        UserProfile profile = userProfileService.getUserProfile(userId);

        return UserProfileResult.from(profile, storageUrlMapper.resolve(profile.getImageUrl()));
    }
}
