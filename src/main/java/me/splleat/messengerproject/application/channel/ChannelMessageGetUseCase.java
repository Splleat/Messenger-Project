package me.splleat.messengerproject.application.channel;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.channel.dto.ChannelMessageGetCommand;
import me.splleat.messengerproject.application.message.dto.MessageResult;
import me.splleat.messengerproject.common.annotation.UseCase;
import me.splleat.messengerproject.domain.channel.ChannelUserSettingService;
import me.splleat.messengerproject.domain.message.Message;
import me.splleat.messengerproject.domain.message.MessageService;
import me.splleat.messengerproject.domain.profile.UserProfile;
import me.splleat.messengerproject.domain.profile.UserProfileService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@UseCase
@RequiredArgsConstructor
public class ChannelMessageGetUseCase {
    private final ChannelUserSettingService channelUserSettingService;
    private final UserProfileService userProfileService;
    private final MessageService messageService;

    @Transactional(readOnly = true)
    public List<MessageResult> execute(ChannelMessageGetCommand command) {
        channelUserSettingService.validateParticipant(command.userId(), command.channelId());

        List<Message> channelMessages = messageService.getChannelMessages(command.channelId());

        List<Long> distinctUserIds = channelMessages.stream()
                .map(Message::getUserId)
                .distinct()
                .toList();

        Map<Long, UserProfile> userProfileMap = userProfileService.getUserProfiles(distinctUserIds).stream()
                .collect(Collectors.toMap(UserProfile::getUserId, profile -> profile));

        return channelMessages.stream()
                .map(message -> MessageResult.from(message, userProfileMap.get(message.getUserId()).getName(), userProfileMap.get(message.getUserId()).getImageUrl()))
                .toList();
    }
}
