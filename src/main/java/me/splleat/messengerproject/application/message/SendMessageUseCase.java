package me.splleat.messengerproject.application.message;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.message.dto.SendMessageCommand;
import me.splleat.messengerproject.application.message.dto.SendMessageResult;
import me.splleat.messengerproject.common.annotation.UseCase;
import me.splleat.messengerproject.domain.channel.Channel;
import me.splleat.messengerproject.domain.channel.ChannelService;
import me.splleat.messengerproject.domain.channel.ChannelUserSettingService;
import me.splleat.messengerproject.domain.member.GroupMemberService;
import me.splleat.messengerproject.domain.message.Message;
import me.splleat.messengerproject.domain.message.MessageService;
import me.splleat.messengerproject.domain.profile.UserProfile;
import me.splleat.messengerproject.domain.profile.UserProfileService;
import me.splleat.messengerproject.domain.user.User;
import me.splleat.messengerproject.domain.user.UserService;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
public class SendMessageUseCase {
    private final UserService userService;
    private final ChannelService channelService;
    private final ChannelUserSettingService channelUserSettingService;
    private final GroupMemberService groupMemberService;
    private final UserProfileService userProfileService;
    private final MessageService messageService;

    @Transactional
    public SendMessageResult execute(SendMessageCommand command) {
        channelUserSettingService.validateParticipant(command.senderId(), command.channelId());

        User userReference = userService.getReference(command.senderId());
        Channel channel = channelService.getChannel(command.channelId());

        Message message = command.toEntity(userReference, channel);

        Message created = messageService.registerWithIdempotency(message);

        UserProfile userProfile = userProfileService.getUserProfile(command.senderId());
        String username = userProfile.getName();
        String profileUrl = userProfile.getImageUrl();

        if (channel.isGroupChannel()) {
            username = groupMemberService.getNickname(command.senderId(), channel.getGroupId())
                    .orElse(username);
        }

        return SendMessageResult.from(created, username, profileUrl);
    }
}
