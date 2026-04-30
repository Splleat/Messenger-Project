package me.splleat.messengerproject.domain.channel;

import me.splleat.messengerproject.domain.channel.exception.ChannelUserSettingAlreadyExistsException;
import me.splleat.messengerproject.domain.channel.exception.ChannelUserSettingNotFoundException;
import me.splleat.messengerproject.domain.user.User;
import me.splleat.messengerproject.infrastructure.persistence.jpa.ChannelUserSettingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class ChannelUserSettingServiceTest {

    @Mock
    private ChannelUserSettingRepository channelUserSettingRepository;

    @InjectMocks
    private ChannelUserSettingService channelUserSettingService;

    @Test
    @DisplayName("이미 존재하는 채널 사용자 설정 정보를 등록하려 하면, ChannelUserSettingAlreadyExistsException이 발생한다.")
    void register_WhenExists_ThrowsException() {
        // given
        long id = 1L;
        User user = mock(User.class);
        Channel channel = mock(Channel.class);
        ChannelUserSetting channelUserSetting = ChannelUserSetting.create(user, channel);

        given(user.getId())
                .willReturn(id);
        given(channel.getId())
                .willReturn(id);
        given(channelUserSettingRepository.existsByUserIdAndChannelId(id, id))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> channelUserSettingService.register(channelUserSetting))
                .isInstanceOf(ChannelUserSettingAlreadyExistsException.class);

        then(channelUserSettingRepository)
                .should(never())
                .save(any(ChannelUserSetting.class));
    }

    @Test
    @DisplayName("빈 리스트나 null을 등록하려고 하면, 아무런 일도 발생하지 않는다.")
    void registerAll_WhenInvalidInput_DoesNothing() {
        // given
        List<ChannelUserSetting> emptyList = Collections.emptyList();
        List<ChannelUserSetting> nullList = null;

        // when & then
        assertDoesNotThrow(() -> channelUserSettingService.registerAll(emptyList));
        assertDoesNotThrow(() -> channelUserSettingService.registerAll(nullList));

        then(channelUserSettingRepository)
                .should(never())
                .saveAll(any());
    }

    @Test
    @DisplayName("채널에 등록된 사용자 설정 정보가 없다면, 빈 리스트를 반환한다.")
    void alreadyJoinedIds_WhenNotExists_ReturnsEmptyList() {
        // when
        List<Long> foundIds = channelUserSettingService.alreadyJoinedIds(1L);

        // then
        assertThat(foundIds)
                .isEmpty();
    }
    
    @Test
    @DisplayName("채널에 참여하고 있는 사용자가 아니라면, ChannelUserSettingNotFoundException이 발생한다.")
    void validateParticipant_WhenNotParticipant_ThrowsException() {
        // given
        long userId = 1L;
        long channelId = 1L;

        given(channelUserSettingRepository.existsByUserIdAndChannelId(userId, channelId))
                .willReturn(false);

        // when & then
        assertThatThrownBy(() -> channelUserSettingService.validateParticipant(userId, channelId))
                .isInstanceOf(ChannelUserSettingNotFoundException.class);
    }

    @Test
    @DisplayName("채널에 참여하고 있는 사용자라면, 예외가 발생하지 않는다.")
    void validateParticipant_WhenIsParticipant_DoesNotThrowException() {
        // given
        long userId = 1L;
        long channelId = 1L;

        given(channelUserSettingRepository.existsByUserIdAndChannelId(userId, channelId))
                .willReturn(true);

        // when & then
        assertDoesNotThrow(() -> channelUserSettingService.validateParticipant(userId, channelId));
    }
}
