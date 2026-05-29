package me.splleat.messengerproject.infrastructure.websocket;

import me.splleat.messengerproject.common.exception.BusinessException;
import me.splleat.messengerproject.common.exception.ErrorCode;
import me.splleat.messengerproject.infrastructure.security.JwtValidator;
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
    private MessageChannel channel;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private StompHandler stompHandler;

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
}
