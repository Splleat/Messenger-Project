package me.splleat.messengerproject.domain.message;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.infrastructure.persistence.jpa.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;

    public Message register(Message message) {
        return messageRepository.save(message);
    }

    // 디버깅용
    public List<Message> getAllMessage(long channelId) {
        return messageRepository.findAllByChannelId(channelId);
    }
}
