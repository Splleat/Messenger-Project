package me.splleat.messengerproject.application.channel;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.common.annotation.UseCase;
import me.splleat.messengerproject.domain.channel.ChannelUserSettingService;
import me.splleat.messengerproject.infrastructure.persistence.querydsl.MessageQueryRepository;
import me.splleat.messengerproject.interfaces.websocket.message.dto.MessageCursorCommand;
import me.splleat.messengerproject.interfaces.websocket.message.dto.MessageCursorNextResponse;

@UseCase
@RequiredArgsConstructor
public class ChannelMessageCursorNextGetUseCase {
    private final MessageQueryRepository messageQueryRepository;
    private final ChannelUserSettingService channelUserSettingService;

    public MessageCursorNextResponse execute(MessageCursorCommand command) {
        channelUserSettingService.validateParticipant(command.userId(), command.channelId());

        return messageQueryRepository.findByNextId(command.channelId(), command.cursorId());
    }
}
