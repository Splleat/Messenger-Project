package me.splleat.messengerproject.application.channel;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.common.annotation.UseCase;
import me.splleat.messengerproject.domain.channel.ChannelUserSettingService;
import me.splleat.messengerproject.infrastructure.persistence.querydsl.MessageQueryRepository;
import me.splleat.messengerproject.interfaces.websocket.message.dto.MessageCursorCommand;
import me.splleat.messengerproject.interfaces.websocket.message.dto.MessageCursorPrevResponse;

@UseCase
@RequiredArgsConstructor
public class ChannelMessageCursorPrevGetUseCase {
    private final MessageQueryRepository messageQueryRepository;
    private final ChannelUserSettingService channelUserSettingService;

    public MessageCursorPrevResponse execute(MessageCursorCommand command) {
        channelUserSettingService.validateParticipant(command.userId(), command.channelId());

        return messageQueryRepository.findByPrevId(command.channelId(), command.cursorId());
    }
}
