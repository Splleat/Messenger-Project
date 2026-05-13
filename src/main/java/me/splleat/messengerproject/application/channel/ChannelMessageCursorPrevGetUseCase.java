package me.splleat.messengerproject.application.channel;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.common.annotation.UseCase;
import me.splleat.messengerproject.common.constant.CursorConstant;
import me.splleat.messengerproject.domain.channel.ChannelUserSettingService;
import me.splleat.messengerproject.infrastructure.persistence.querydsl.MessageQueryRepository;
import me.splleat.messengerproject.interfaces.websocket.message.dto.MessageCursorCommand;
import me.splleat.messengerproject.interfaces.websocket.message.dto.MessageCursorPrevResponse;
import me.splleat.messengerproject.interfaces.websocket.message.dto.MessageResponse;
import org.springframework.data.domain.Slice;

@UseCase
@RequiredArgsConstructor
public class ChannelMessageCursorPrevGetUseCase {
    private final MessageQueryRepository messageQueryRepository;
    private final ChannelUserSettingService channelUserSettingService;

    public MessageCursorPrevResponse execute(MessageCursorCommand command) {
        channelUserSettingService.validateParticipant(command.userId(), command.channelId());

        Slice<MessageResponse> prev = messageQueryRepository.findByPrevId(command.channelId(), command.cursorId(), CursorConstant.MESSAGE_SIZE);

        return MessageCursorPrevResponse.from(prev);
    }
}
