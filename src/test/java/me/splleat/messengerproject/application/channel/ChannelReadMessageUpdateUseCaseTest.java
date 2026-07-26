package me.splleat.messengerproject.application.channel;

import me.splleat.messengerproject.domain.channel.ChannelUserSetting;
import me.splleat.messengerproject.domain.channel.ChannelUserSettingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ChannelReadMessageUpdateUseCaseTest {

    @Mock
    private ChannelUserSettingService channelUserSettingService;

    @InjectMocks
    private ChannelReadMessageUpdateUseCase channelReadMessageUpdateUseCase;

    @Test
    @DisplayName("해당 채널에서의 사용자의 마지막 읽은 메시지 ID를 갱신한다.")
    void execute_WhenUpdated_UpdatesLastReadMessageId() {
        // given
        long userId = 1L;
        long channelId = 1L;
        long lastReadMessageId = 1L;
        ChannelUserSetting setting = mock(ChannelUserSetting.class);

        given(channelUserSettingService.getChannelUserSetting(userId, channelId))
                .willReturn(setting);

        // when
        channelReadMessageUpdateUseCase.execute(userId, channelId, lastReadMessageId);

        // then
        then(setting)
                .should()
                .updateLastReadMessage(lastReadMessageId);
    }
}