package me.splleat.messengerproject.application.profile;
 
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.splleat.messengerproject.application.profile.dto.UserProfileImageUpdateCommand;
import me.splleat.messengerproject.common.annotation.UseCase;
import me.splleat.messengerproject.domain.user.UserProfileService;
import me.splleat.messengerproject.infrastructure.cache.UserProfileCacheEvictor;
import me.splleat.messengerproject.infrastructure.storage.S3Service;
 
@Slf4j
@UseCase
@RequiredArgsConstructor
public class UserProfileImageUpdateUseCase {
    private final S3Service s3Service;
    private final UserProfileService userProfileService;
    private final UserProfileCacheEvictor cacheEvictor;
 
    public void execute(UserProfileImageUpdateCommand command) {
        String oldImageKey = userProfileService.updateImageUrl(command.userId(), command.newImageKey());

        cacheEvictor.evictMyProfile(command.userId());
        cacheEvictor.evictUserProfile(command.userId());
 
        // 이전 프로필 이미지가 존재하는 경우
        if (oldImageKey != null && !oldImageKey.isBlank()) {
            try {
                s3Service.deleteObject(oldImageKey);
            } catch (Exception e) {
                log.warn("구버전 프로필 이미지 삭제 실패 | 오브젝트 키: {}: {}", oldImageKey, e.getMessage());
            }
        }
    }
}
