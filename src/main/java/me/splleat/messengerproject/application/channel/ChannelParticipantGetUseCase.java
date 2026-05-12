package me.splleat.messengerproject.application.channel;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.channel.dto.ChannelParticipantGetCommand;
import me.splleat.messengerproject.common.annotation.UseCase;
import me.splleat.messengerproject.domain.channel.ChannelUserSettingService;
import me.splleat.messengerproject.domain.profile.UserProfile;
import me.splleat.messengerproject.domain.profile.UserProfileService;
import me.splleat.messengerproject.interfaces.rest.channel.dto.ChannelParticipantResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@UseCase
@RequiredArgsConstructor
public class ChannelParticipantGetUseCase {
    private final ChannelUserSettingService channelUserSettingService;
    private final UserProfileService userProfileService;

    @Transactional(readOnly = true)
    public List<ChannelParticipantResponse> execute(ChannelParticipantGetCommand command) {
        channelUserSettingService.validateParticipant(command.userId(), command.channelId());

        List<Long> participantIds = channelUserSettingService.getJoinedUserIds(command.channelId());

        List<UserProfile> userProfiles = userProfileService.getUserProfiles(participantIds);

        return userProfiles.stream()
                .map(ChannelParticipantResponse::from)
                .toList();
    }
}
