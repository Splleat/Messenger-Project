package me.splleat.messengerproject.application.message;

import me.splleat.messengerproject.application.message.dto.MessageCreateCommand;
import me.splleat.messengerproject.domain.channel.Channel;
import me.splleat.messengerproject.domain.channel.ChannelService;
import me.splleat.messengerproject.domain.channel.ChannelUserSettingService;
import me.splleat.messengerproject.domain.member.GroupMemberService;
import me.splleat.messengerproject.domain.message.Message;
import me.splleat.messengerproject.domain.message.MessageService;
import me.splleat.messengerproject.domain.message.MessageType;
import me.splleat.messengerproject.domain.profile.UserProfile;
import me.splleat.messengerproject.domain.profile.UserProfileService;
import me.splleat.messengerproject.domain.user.User;
import me.splleat.messengerproject.domain.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class SendMessageUseCaseTest {

    @Mock
    private UserService userService;

    @Mock
    private ChannelService channelService;

    @Mock
    private ChannelUserSettingService channelUserSettingService;

    @Mock
    private GroupMemberService groupMemberService;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private MessageService messageService;

    @InjectMocks
    private SendMessageUseCase sendMessageUseCase;

    @Test
    @DisplayName("올바른 명령이 주어지면 메시지를 저장하고 결과를 반환한다.")
    void execute_WhenValidCommand_SavesAndReturnMessage() {
        // given
        long userId = 1L;
        long channelId = 1L;
        MessageCreateCommand command = new MessageCreateCommand(userId, channelId, "test", UUID.randomUUID(), MessageType.DIRECT, null);
        User user = mock(User.class);
        Channel channel = mock(Channel.class);
        UserProfile profile = mock(UserProfile.class);
        Message message = mock(Message.class);

        given(userService.getReference(userId))
                .willReturn(user);
        given(channelService.getChannel(channelId))
                .willReturn(channel);
        given(messageService.registerWithIdempotency(any(Message.class)))
                .willReturn(message);
        given(userProfileService.getUserProfile(userId))
                .willReturn(profile);
        given(profile.getName())
                .willReturn("testUser");
        given(profile.getImageUrl())
                .willReturn("testUrl");
        given(channel.isGroupChannel())
                .willReturn(false);

        // when
        assertDoesNotThrow(() -> sendMessageUseCase.execute(command));

        // then
        then(channelUserSettingService)
                .should()
                .validateParticipant(userId, channelId);

        then(messageService)
                .should()
                .registerWithIdempotency(any(Message.class));

        then(userProfileService)
                .should()
                .getUserProfile(userId);
    }
}