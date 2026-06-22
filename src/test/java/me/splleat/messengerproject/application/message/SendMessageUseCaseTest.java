package me.splleat.messengerproject.application.message;

import me.splleat.messengerproject.application.message.dto.MessageCreateCommand;
import me.splleat.messengerproject.common.util.StorageUrlMapper;
import me.splleat.messengerproject.domain.channel.Channel;
import me.splleat.messengerproject.domain.channel.ChannelService;
import me.splleat.messengerproject.domain.channel.ChannelUserSetting;
import me.splleat.messengerproject.domain.channel.ChannelUserSettingService;
import me.splleat.messengerproject.domain.message.*;
import me.splleat.messengerproject.domain.space.SpaceMemberService;
import me.splleat.messengerproject.domain.user.UserProfile;
import me.splleat.messengerproject.domain.user.UserProfileService;
import me.splleat.messengerproject.infrastructure.message.outbox.MessageEvent;
import me.splleat.messengerproject.support.fixture.ChannelFixture;
import me.splleat.messengerproject.support.fixture.UserProfileFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class SendMessageUseCaseTest {

    @Mock
    private ChannelService channelService;

    @Mock
    private ChannelUserSettingService channelUserSettingService;

    @Mock
    private SpaceMemberService spaceMemberService;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private MessageService messageService;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Mock
    private AttachmentService attachmentService;

    @Mock
    private StorageUrlMapper storageUrlMapper;

    @InjectMocks
    private SendMessageUseCase sendMessageUseCase;

    @Test
    @DisplayName("올바른 명령이 주어지면 메시지를 저장하고 결과를 반환한다.")
    void execute_WhenValidCommand_SavesAndReturnMessage() {
        // given
        long channelId = 1L;
        Channel channel = ChannelFixture.directChannel();
        UserProfile profile = UserProfileFixture.defaultUserProfile(1L);
        MessageCreateCommand command = new MessageCreateCommand(profile.getUserId(), channelId, "test", UUID.randomUUID(), MessageType.DIRECT, null, null);
        Message message = mock(Message.class);
        MessageRegistration messageResult = MessageRegistration.of(message, true);
        ChannelUserSetting setting = mock(ChannelUserSetting.class);

        given(channelUserSettingService.getChannelUserSetting(command.userId(), command.channelId()))
                .willReturn(setting);
        given(channelService.getChannel(channelId))
                .willReturn(channel);
        given(messageService.registerWithIdempotency(any(Message.class)))
                .willReturn(messageResult);
        given(userProfileService.getUserProfile(command.userId()))
                .willReturn(profile);

        // when
        assertDoesNotThrow(() -> sendMessageUseCase.execute(command));

        // then
        then(channelUserSettingService)
                .should()
                .getChannelUserSetting(command.userId(), command.channelId());

        then(messageService)
                .should()
                .registerWithIdempotency(any(Message.class));

        then(userProfileService)
                .should()
                .getUserProfile(command.userId());

        then(applicationEventPublisher)
                .should()
                .publishEvent(any(MessageEvent.class));
    }

    @Test
    @DisplayName("이미 저장된 메시지라면, 이벤트가 발행되지 않는다.")
    void execute_WhenMessageAlreadyExists_DoesNotPublishEvent() {
        // given
        long channelId = 1L;
        Channel channel = ChannelFixture.directChannel();
        UserProfile profile = UserProfileFixture.defaultUserProfile(1L);
        MessageCreateCommand command = new MessageCreateCommand(profile.getUserId(), channelId, "test", UUID.randomUUID(), MessageType.DIRECT, null, null);
        Message message = mock(Message.class);
        MessageRegistration messageResult = MessageRegistration.of(message, false);
        ChannelUserSetting setting = mock(ChannelUserSetting.class);

        given(channelUserSettingService.getChannelUserSetting(command.userId(), command.channelId()))
                .willReturn(setting);
        given(channelService.getChannel(channelId))
                .willReturn(channel);
        given(messageService.registerWithIdempotency(any(Message.class)))
                .willReturn(messageResult);
        given(userProfileService.getUserProfile(command.userId()))
                .willReturn(profile);

        // when
        assertDoesNotThrow(() -> sendMessageUseCase.execute(command));

        // then
        then(applicationEventPublisher)
                .should(never())
                .publishEvent(any(MessageEvent.class));
    }
}