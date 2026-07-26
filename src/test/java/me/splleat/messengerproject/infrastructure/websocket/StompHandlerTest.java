package me.splleat.messengerproject.infrastructure.websocket;

import me.splleat.messengerproject.common.exception.BusinessException;
import me.splleat.messengerproject.common.exception.ErrorCode;
import me.splleat.messengerproject.domain.channel.ChannelUserSettingService;
import me.splleat.messengerproject.infrastructure.security.JwtValidator;
import me.splleat.messengerproject.infrastructure.security.UserPrincipal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StompHandlerTest {

    @Mock
    private JwtValidator jwtValidator;

    @Mock
    private ChannelUserSettingService channelUserSettingService;

    @Mock
    private MessageChannel channel;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private StompHandler stompHandler;

    private Message<byte[]> subscribeMessage(String destination, long userId) {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        accessor.setDestination(destination);
        accessor.setUser(new UsernamePasswordAuthenticationToken(UserPrincipal.create(userId, false), null));

        return MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
    }

    @Test
    @DisplayName("preSend: CONNECT 명령 시 유효한 토큰이 있으면 인증 객체를 설정한다.")
    void preSend_WhenValidToken_SetsAuthentication() {
        // given
        String token = "valid.jwt.token";
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        accessor.setLeaveMutable(true);
        accessor.setNativeHeader("Authorization", "Bearer " + token);
        Message<byte[]> message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

        given(jwtValidator.validateAndGetAuthentication(token))
                .willReturn(authentication);

        // when
        Message<?> result = stompHandler.preSend(message, channel);

        // then
        assertThat(result)
                .isNotNull();

        StompHeaderAccessor resultAccessor = MessageHeaderAccessor.getAccessor(result, StompHeaderAccessor.class);

        assertThat(resultAccessor.getUser())
                .isEqualTo(authentication);

        then(jwtValidator)
                .should()
                .validateAndGetAuthentication(token);
    }

    @Test
    @DisplayName("preSend: CONNECT 명령 시 Authorization 헤더가 없으면 예외가 발생한다.")
    void preSend_WhenWithoutAuthorizationHeader_ThrowsException() {
        // given
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        Message<byte[]> message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

        // when & then
        assertThatThrownBy(() -> stompHandler.preSend(message, channel))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.UNAUTHORIZED);
    }

    @Test
    @DisplayName("preSend: CONNECT 명령 시 Bearer 형식이 아니면 예외가 발생한다.")
    void preSend_WhenInvalidHeaderFormat_ThrowsException() {
        // given
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        accessor.setNativeHeader("Authorization", "InvalidToken " + "token");
        Message<byte[]> message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

        // when & then
        assertThatThrownBy(() -> stompHandler.preSend(message, channel))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.UNAUTHORIZED);
    }

    @Test
    @DisplayName("preSend: CONNECT 이외의 명령은 검증 없이 통과시킨다.")
    void preSend_withOtherCommand_passesThrough() {
        // given
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SEND);
        Message<byte[]> message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

        // when
        Message<?> result = stompHandler.preSend(message, channel);

        // then
        assertThat(result)
                .isEqualTo(message);

        then(jwtValidator)
                .should(never())
                .validateAndGetAuthentication(anyString());
    }

    @Test
    @DisplayName("preSend: SUBSCRIBE 명령 시 채널 참여자면 통과시킨다.")
    void preSend_WhenSubscribeToOwnChannel_Passes() {
        // given
        Message<byte[]> message = subscribeMessage("/sub/channels/1/messages", 10L);

        // when
        Message<?> result = stompHandler.preSend(message, channel);

        // then
        assertThat(result).isNotNull();
        then(channelUserSettingService).should().validateParticipant(10L, 1L);
    }

    @Test
    @DisplayName("preSend: SUBSCRIBE 명령 시 채널 참여자가 아니면 예외가 발생한다.")
    void preSend_WhenSubscribeToNonParticipantChannel_ThrowsException() {
        // given
        Message<byte[]> message = subscribeMessage("/sub/channels/1/messages", 10L);

        doThrow(new BusinessException(ErrorCode.CHANNEL_USER_SETTING_NOT_FOUND))
                .when(channelUserSettingService).validateParticipant(10L, 1L);

        // when & then
        assertThatThrownBy(() -> stompHandler.preSend(message, channel))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CHANNEL_USER_SETTING_NOT_FOUND);
    }

    @Test
    @DisplayName("preSend: SUBSCRIBE 명령 시 본인 알림 목적지면 통과시킨다.")
    void preSend_WhenSubscribeToOwnNotifications_Passes() {
        // given
        Message<byte[]> message = subscribeMessage("/sub/users/10/notifications", 10L);

        // when
        Message<?> result = stompHandler.preSend(message, channel);

        // then
        assertThat(result).isNotNull();
        then(channelUserSettingService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("preSend: SUBSCRIBE 명령 시 다른 사용자의 알림 목적지면 예외가 발생한다.")
    void preSend_WhenSubscribeToOtherUsersNotifications_ThrowsException() {
        // given
        Message<byte[]> message = subscribeMessage("/sub/users/99/notifications", 10L);

        // when & then
        assertThatThrownBy(() -> stompHandler.preSend(message, channel))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FORBIDDEN);
    }

    @Test
    @DisplayName("preSend: SUBSCRIBE 명령 시 알 수 없는 목적지면 예외가 발생한다.")
    void preSend_WhenSubscribeToUnknownDestination_ThrowsException() {
        // given
        Message<byte[]> message = subscribeMessage("/sub/unknown", 10L);

        // when & then
        assertThatThrownBy(() -> stompHandler.preSend(message, channel))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FORBIDDEN);
    }
}
