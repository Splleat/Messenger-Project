package me.splleat.messengerproject.application.channel;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.channel.dto.ChannelParticipantResult;
import me.splleat.messengerproject.common.annotation.UseCase;
import me.splleat.messengerproject.domain.channel.ChannelUserSettingService;
import me.splleat.messengerproject.domain.profile.UserProfile;
import me.splleat.messengerproject.domain.profile.UserProfileService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@UseCase
@RequiredArgsConstructor
public class ChannelParticipantGetUseCase {
    private final ChannelUserSettingService channelUserSettingService;
    private final UserProfileService userProfileService;

    @Transactional(readOnly = true)
    public List<ChannelParticipantResult> execute(long userId, long channelId) {
        channelUserSettingService.validateParticipant(userId, channelId);

        List<Long> participantIds = channelUserSettingService.getJoinedUserIds(channelId);

        List<UserProfile> userProfiles = userProfileService.getUserProfiles(participantIds);

        return userProfiles.stream()
                .map(ChannelParticipantResult::from)
                .toList();
    }
}
