package me.splleat.messengerproject.domain.message;

import me.splleat.messengerproject.infrastructure.persistence.jpa.MessageRepository;
import me.splleat.messengerproject.support.fixture.MessageFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private MessageService messageService;

    @Test
    @DisplayName("메시지가 새로 저장되면, isCreated가 true이다.")
    void registerWithIdempotency_WhenSaved_IsCreatedTrue() {
        // given
        Message message = MessageFixture.defaultMessage(1L, 1L);

        given(messageRepository.findByIdemPotencyKey(message.getIdemPotencyKey()))
                .willReturn(Optional.empty());

        // when
        MessageRegistration result = messageService.registerWithIdempotency(message);

        // then
        assertThat(result.isCreated())
                .isTrue();
    }

    @Test
    @DisplayName("이미 저장된 메시지라면, isCreated가 false이다.")
    void registerWithIdempotency_WhenAlreadyExists_IsCreatedFalse() {
        // given
        Message message = MessageFixture.defaultMessage(1L, 1L);

        given(messageRepository.findByIdemPotencyKey(message.getIdemPotencyKey()))
                .willReturn(Optional.of(message));

        // when
        MessageRegistration result = messageService.registerWithIdempotency(message);

        // then
        assertThat(result.isCreated())
                .isFalse();

        then(messageRepository)
                .should(never())
                .save(message);
    }
}