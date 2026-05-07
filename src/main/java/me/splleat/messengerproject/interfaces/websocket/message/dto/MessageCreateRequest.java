package me.splleat.messengerproject.interfaces.websocket.message.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import me.splleat.messengerproject.application.message.dto.MessageCreateCommand;
import me.splleat.messengerproject.domain.message.MessageType;

import java.util.UUID;

public record MessageCreateRequest(
        @NotBlank(message = "메시지 내용은 필수 입력값입니다.")
        @Size(max = 1000, message = "메시지 내용은 1000자 이하로 입력해야 합니다.")
        String content,

        @NotBlank(message = "멱등성 키는 필수 입력값입니다.")
        String idempotencyKey,

        @NotBlank(message = "메시지 타입은 필수 입력값입니다.")
        String type,

        Long parentMessageId
) {
    public MessageCreateCommand toCommand(long senderId, long channelId) {
        return new MessageCreateCommand(
                senderId,
                channelId,
                content,
                UUID.fromString(idempotencyKey),
                MessageType.from(type),
                parentMessageId
        );
    }
}
