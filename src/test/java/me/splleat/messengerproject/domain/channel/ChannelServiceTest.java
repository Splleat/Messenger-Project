package me.splleat.messengerproject.domain.channel;

import me.splleat.messengerproject.domain.channel.exception.ChannelNotFoundException;
import me.splleat.messengerproject.infrastructure.persistence.jpa.ChannelRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ChannelServiceTest {

    @Mock
    private ChannelRepository channelRepository;

    @InjectMocks
    private ChannelService channelService;

    @Test
    @DisplayName("채널을 등록하면, 등록된 채널의 정보가 반환된다.")
    void register_WhenValid_ReturnsRegisteredChannel() {
        // given
        Channel channel = Channel.create(null, "test", ChannelType.TEXT);
        given(channelRepository.save(channel))
                .willReturn(channel);

        // when
        Channel found = channelService.register(channel);

        // then
        assertThat(found)
                .isNotNull()
                .isEqualTo(channel);
    }

    @Test
    @DisplayName("존재하지 않는 채널을 조회하려고 하면, ChannelNotFoundException이 발생한다.")
    void getChannel_WhenNotExists_ThrowsException() {
        // given
        long channelId = 1L;

        given(channelRepository.findById(channelId))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> channelService.getChannel(channelId))
                .isInstanceOf(ChannelNotFoundException.class);
    }

}