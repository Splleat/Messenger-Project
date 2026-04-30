package me.splleat.messengerproject.application.channel;

import me.splleat.messengerproject.application.channel.dto.DirectChannelCreateCommand;
import me.splleat.messengerproject.domain.channel.*;
import me.splleat.messengerproject.domain.user.User;
import me.splleat.messengerproject.domain.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class DirectChannelCreateUseCaseTest {

    @Mock
    private UserService userService;

    @Mock
    private ChannelService channelService;

    @Mock
    private ChannelUserSettingService channelUserSettingService;

    @InjectMocks
    private DirectChannelCreateUseCase directChannelCreateUseCase;

    @Test
    @DisplayName("올바른 명령이 주어지면 다이렉트 채널을 생성하고 유저 설정을 등록한다.")
    void execute_WhenValidCommand_CreatesDirectChannel() {
        // given
        long userId = 1L;
        String channelName = "testChannel";
        DirectChannelCreateCommand command = new DirectChannelCreateCommand(userId, channelName, ChannelType.TEXT);
        User user = mock(User.class);
        Channel channel = mock(Channel.class);

        given(userService.getUser(command.userId()))
                .willReturn(user);
        given(channelService.register(any(Channel.class)))
                .willReturn(channel);

        // when
        assertDoesNotThrow(() -> directChannelCreateUseCase.execute(command));

        // then
        then(userService)
                .should()
                .getUser(userId);

        then(channelService)
                .should()
                .register(any(Channel.class));

        then(channelUserSettingService)
                .should()
                .register(any(ChannelUserSetting.class));
    }
}