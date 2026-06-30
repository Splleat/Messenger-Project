package me.splleat.messengerproject.domain.channel;

import me.splleat.messengerproject.common.exception.BusinessException;
import me.splleat.messengerproject.common.exception.ErrorCode;
import me.splleat.messengerproject.infrastructure.persistence.jpa.ChannelUserSettingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class ChannelUserSettingServiceTest {

    @Mock
    private ChannelUserSettingRepository channelUserSettingRepository;

    @InjectMocks
    private ChannelUserSettingService channelUserSettingService;

    @Test
    @DisplayName("이미 존재하는 채널 설정 정보를 등록하려 하면, ChannelUserSettingAlreadyExistsException이 발생한다.")
    void register_WhenExists_ThrowsException() {
        // given
        long userId = 1L;
        long channelId = 1L;
        ChannelUserSetting channelUserSetting = ChannelUserSetting.create(userId, channelId);

        given(channelUserSettingRepository.existsByUserIdAndChannelId(userId, channelId))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> channelUserSettingService.register(channelUserSetting))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CHANNEL_USER_SETTING_ALREADY_EXISTS);

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
    @DisplayName("채널에 등록된 설정 정보가 없다면, 빈 리스트를 반환한다.")
    void alreadyJoinedIds_WhenNotExists_ReturnsEmptyList() {
        // when
        List<Long> foundIds = channelUserSettingService.alreadyJoinedIds(1L, List.of(1L, 2L, 3L));

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
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CHANNEL_USER_SETTING_NOT_FOUND);
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

    @Test
    @DisplayName("존재하지 않는 채널 설정 정보를 조회하려 하면, ChannelUserSettingNotFoundException이 발생한다.")
    void getChannelUserSetting_WhenNotExists_ThrowsException() {
        // given
        long userId = 1L;
        long channelId = 1L;

        given(channelUserSettingRepository.findByUserIdAndChannelId(userId, channelId))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> channelUserSettingService.getChannelUserSetting(userId, channelId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CHANNEL_USER_SETTING_NOT_FOUND);
    }

    @Test
    @DisplayName("나가려는 채널의 설정 정보가 존재하지 않으면 ChannelUserSettingNotFoundException이 발생한다.")
    void leaveChannel_WhenNotExists_ThrowsException() {
        // given
        long userId = 1L;
        long channelId = 1L;

        // when & then
        assertThatThrownBy(() -> channelUserSettingService.leaveChannel(userId, channelId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CHANNEL_USER_SETTING_NOT_FOUND);
    }

    @Test
    @DisplayName("가져오려는 채널 설정 정보가 없으면 새로 생성한다.")
    void registerIfAbsent_WhenNotExists_ReturnsNewChannelUserSetting() {
        // given
        long userId = 1L;
        long channelId = 1L;

        given(channelUserSettingRepository.findByUserIdAndChannelId(userId, channelId))
                .willReturn(Optional.empty());

        ChannelUserSetting expected = ChannelUserSetting.create(userId, channelId);

        given(channelUserSettingRepository.save(any(ChannelUserSetting.class)))
                .willReturn(expected);

        // when
        ChannelUserSetting found = channelUserSettingService.registerIfAbsent(userId, channelId);

        // then
        assertThat(found)
                .isEqualTo(expected);

        then(channelUserSettingRepository)
                .should()
                .save(any(ChannelUserSetting.class));
    }
}
