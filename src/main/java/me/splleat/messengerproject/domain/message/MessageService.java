package me.splleat.messengerproject.domain.message;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.common.exception.BusinessException;
import me.splleat.messengerproject.common.exception.ErrorCode;
import me.splleat.messengerproject.infrastructure.persistence.jpa.MessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;

    @Transactional
    public MessageRegistration registerWithIdempotency(Message message) {
        Optional<Message> result = messageRepository.findByIdemPotencyKey(message.getIdemPotencyKey());

        return result.map(m -> MessageRegistration.of(m, false))
                .orElseGet(() -> {
                    Message saved = messageRepository.save(message);
                    return MessageRegistration.of(saved, true);
                });
    }

    @Transactional(readOnly = true)
    public Message getUserMessage(long userId, long messageId) {
        return messageRepository.findByUserIdAndId(userId, messageId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MESSAGE_NOT_FOUND));

    }
}