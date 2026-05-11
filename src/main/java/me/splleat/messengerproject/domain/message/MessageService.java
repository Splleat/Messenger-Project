package me.splleat.messengerproject.domain.message;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.infrastructure.persistence.jpa.MessageRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Message registerWithIdempotency(Message message) {
        try {
            return messageRepository.save(message);
        } catch (DataIntegrityViolationException _) {
            return messageRepository.findByIdemPotencyKey(message.getIdemPotencyKey());
        }
    }

    // TODO: 커서 기반 페이징 추가
    @Transactional(readOnly = true)
    public List<Message> getChannelMessages(long channelId) {
        return messageRepository.findAllByChannelId(channelId);
    }
}
