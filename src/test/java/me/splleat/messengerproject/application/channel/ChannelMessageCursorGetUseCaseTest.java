package me.splleat.messengerproject.application.channel;

import me.splleat.messengerproject.application.channel.dto.ChannelMessageCursorCommand;
import me.splleat.messengerproject.application.channel.dto.ChannelMessagePageResult;
import me.splleat.messengerproject.domain.channel.ChannelUserSetting;
import me.splleat.messengerproject.domain.channel.ChannelUserSettingService;
import me.splleat.messengerproject.infrastructure.persistence.querydsl.MessageQueryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ChannelMessageCursorGetUseCaseTest {

    @Mock
    private MessageQueryRepository messageQueryRepository;

    @Mock
    private ChannelUserSettingService channelUserSettingService;

    @InjectMocks
    private ChannelMessageCursorGetUseCase channelMessageCursorGetUseCase;

    static class TestFixture {
        static final long USER_ID = 1L;
        static final long CHANNEL_ID = 1L;
        static final long CURSOR_ID = 1L;

        static ChannelMessageCursorCommand defaultCommand(String direction) {
            return new ChannelMessageCursorCommand(USER_ID, CHANNEL_ID, CURSOR_ID, CursorDirection.from(direction));
        }
    }

    @Test
    @DisplayName("커서 이전 페이지를 요청하면 이전 페이지를 반환하고, 마지막 읽은 메시지 ID를 업데이트하지 않는다.")
    void execute_WhenCursorPrevPage_ReturnsPrevPageAndDoesNotUpdateLastReadMessageId() {
        // given
        long prevCursorId = 50L;
        ChannelMessageCursorCommand command = TestFixture.defaultCommand(CursorDirection.PREV.name());
        ChannelUserSetting setting = ChannelUserSetting.create(TestFixture.USER_ID, TestFixture.CHANNEL_ID);
        ChannelMessagePageResult expected = new ChannelMessagePageResult(List.of(), false, prevCursorId);

        given(channelUserSettingService.getChannelUserSetting(command.userId(), command.channelId()))
                .willReturn(setting);
        given(messageQueryRepository.findByPrevId(command.channelId(), command.cursorId()))
                .willReturn(expected);

        // when
        ChannelMessagePageResult actualResult = channelMessageCursorGetUseCase.execute(command);

        // then
        assertThat(actualResult)
                .isEqualTo(expected);
        assertThat(setting.getLastReadMessageId())
                .isNull();
    }

    @Test
    @DisplayName("커서 다음 페이지를 요청하면 다음 페이지를 반환하고, 마지막 읽은 메시지 ID를 업데이트한다.")
    void execute_WhenCursorNextPage_ReturnNextPageAndUpdateLastReadMessageId() {
        // given
        long nextCursorId = 100L;
        ChannelUserSetting setting = ChannelUserSetting.create(TestFixture.USER_ID, TestFixture.CHANNEL_ID);
        ChannelMessageCursorCommand command = TestFixture.defaultCommand(CursorDirection.NEXT.name());
        ChannelMessagePageResult expected = new ChannelMessagePageResult(List.of(), false, nextCursorId);

        given(channelUserSettingService.getChannelUserSetting(command.userId(), command.channelId()))
                .willReturn(setting);
        given(messageQueryRepository.findByNextId(command.channelId(), command.cursorId()))
                .willReturn(expected);

        // when
        ChannelMessagePageResult actualResult = channelMessageCursorGetUseCase.execute(command);

        // then
        assertThat(actualResult)
                .isEqualTo(expected);
        assertThat(setting.getLastReadMessageId())
                .isEqualTo(nextCursorId);
    }
}