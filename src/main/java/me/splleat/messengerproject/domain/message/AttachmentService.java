package me.splleat.messengerproject.domain.message;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.infrastructure.persistence.jpa.AttachmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttachmentService {
    private final AttachmentRepository attachmentRepository;

    @Transactional
    public void registerAll(List<Attachment> attachments) {
        attachmentRepository.saveAll(attachments);
    }

    @Transactional(readOnly = true)
    public List<Attachment> getAttachments(long messageId) {
        return attachmentRepository.findByMessageId(messageId);
    }
}
