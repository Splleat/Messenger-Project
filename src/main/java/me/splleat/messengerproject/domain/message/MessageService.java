package me.splleat.messengerproject.domain.message;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.infrastructure.persistence.jpa.MessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;

    @Transactional
    public Message registerWithIdempotency(Message message) {
        return messageRepository.findByIdemPotencyKey(message.getIdemPotencyKey())
                .orElseGet(() -> messageRepository.save(message));
    }
}