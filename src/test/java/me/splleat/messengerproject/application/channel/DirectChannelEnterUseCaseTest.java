package me.splleat.messengerproject.application.channel;

import me.splleat.messengerproject.application.channel.dto.ChannelEnterResult;
import me.splleat.messengerproject.domain.channel.ChannelUserSetting;
import me.splleat.messengerproject.domain.channel.ChannelUserSettingService;
import me.splleat.messengerproject.infrastructure.persistence.querydsl.MessageQueryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class DirectChannelEnterUseCaseTest {

    @Mock
    private MessageQueryRepository messageQueryRepository;

    @Mock
    private ChannelUserSettingService channelUserSettingService;

    @InjectMocks
    private DirectChannelEnterUseCase directChannelEnterUseCase;

    @Test
    @DisplayName("마지막으로 읽은 메시지 ID가 없는 경우, 최신 메시지 목록을 조회하고 읽은 위치를 갱신한다.")
    void execute_WhenLastReadMessageIdIsNull_FindsByNewest() {
        // given
        long userId = 1L;
        long channelId = 1L;
        long nextCursorId = 10L;
        ChannelUserSetting setting = mock(ChannelUserSetting.class);
        ChannelEnterResult result = mock(ChannelEnterResult.class);

        given(channelUserSettingService.getChannelUserSetting(userId, channelId))
                .willReturn(setting);
        given(setting.getLastReadMessageId())
                .willReturn(null);
        given(messageQueryRepository.findByNewest(channelId))
                .willReturn(result);
        given(result.nextCursorId())
                .willReturn(nextCursorId);

        // when
        ChannelEnterResult actual = directChannelEnterUseCase.execute(userId, channelId);

        // then
        assertThat(actual)
                .isEqualTo(result);
        then(setting)
                .should()
                .updateLastReadMessage(nextCursorId);
    }

    @Test
    @DisplayName("마지막으로 읽은 메시지 ID가 있는 경우, 해당 ID 주변 메시지 목록을 조회하고 읽은 위치를 갱신한다.")
    void execute_WhenLastReadMessageIdIsPresent_FindsByAroundId() {
        // given
        long userId = 1L;
        long channelId = 1L;
        long lastReadId = 5L;
        long nextCursorId = 10L;
        ChannelUserSetting setting = mock(ChannelUserSetting.class);
        ChannelEnterResult result = mock(ChannelEnterResult.class);

        given(channelUserSettingService.getChannelUserSetting(userId, channelId))
                .willReturn(setting);
        given(setting.getLastReadMessageId())
                .willReturn(lastReadId);
        given(messageQueryRepository.findByAroundId(channelId, lastReadId))
                .willReturn(result);
        given(result.nextCursorId())
                .willReturn(nextCursorId);

        // when
        ChannelEnterResult actual = directChannelEnterUseCase.execute(userId, channelId);

        // then
        assertThat(actual)
                .isEqualTo(result);
        then(setting)
                .should()
                .updateLastReadMessage(nextCursorId);
    }
}
