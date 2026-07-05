package me.splleat.messengerproject.infrastructure.message.event;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.notification.NotificationNewMessageUseCase;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {
    private final NotificationNewMessageUseCase notificationNewMessageUseCase;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleNotification(MessageCreatedEvent event) {
        notificationNewMessageUseCase.execute(event.message());
    }
}
