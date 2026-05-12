package me.splleat.messengerproject.application.channel;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.channel.dto.GroupChannelMessageGetCommand;
import me.splleat.messengerproject.common.annotation.UseCase;
import me.splleat.messengerproject.domain.channel.ChannelService;
import me.splleat.messengerproject.domain.channel.ChannelUserSettingService;
import me.splleat.messengerproject.domain.member.GroupMemberService;
import me.splleat.messengerproject.domain.message.Message;
import me.splleat.messengerproject.domain.message.MessageService;
import me.splleat.messengerproject.domain.profile.UserProfile;
import me.splleat.messengerproject.domain.profile.UserProfileService;
import me.splleat.messengerproject.interfaces.websocket.message.dto.MessageResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@UseCase
@RequiredArgsConstructor
public class GroupChannelMessageGetUseCase {
    private final ChannelService channelService;
    private final ChannelUserSettingService channelUserSettingService;
    private final GroupMemberService groupMemberService;
    private final MessageService messageService;
    private final UserProfileService userProfileService;

    @Transactional(readOnly = true)
    public List<MessageResponse> execute(GroupChannelMessageGetCommand command) {
        groupMemberService.validateParticipant(command.userId(), command.groupId());

        channelService.validateInGroup(command.groupId(), command.channelId());

        channelUserSettingService.registerIfAbsent(command.userId(), command.channelId());

        List<Long> userIds = messageService.getUserIdsByChannelMessages(command.channelId());

        Map<Long, UserProfile> profileMap = userProfileService.getUserProfileMap(userIds);

        List<Message> messages = messageService.getChannelMessages(command.channelId());

        return messages.stream()
                .map(message -> {
                    UserProfile profile = profileMap.get(message.getUserId());

                    String username = (profile != null) ? profile.getName() : "탈퇴한 사용자";
                    String profileUrl = (profile != null) ? profile.getImageUrl() : null;

                    return MessageResponse.of(username, profileUrl, message);
                })
                .toList();
    }
}
