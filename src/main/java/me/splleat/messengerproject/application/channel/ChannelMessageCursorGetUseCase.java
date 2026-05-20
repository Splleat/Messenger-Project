package me.splleat.messengerproject.application.channel;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.common.annotation.UseCase;
import me.splleat.messengerproject.common.exception.BusinessException;
import me.splleat.messengerproject.common.exception.ErrorCode;
import me.splleat.messengerproject.domain.channel.ChannelUserSettingService;
import me.splleat.messengerproject.infrastructure.persistence.querydsl.MessageQueryRepository;
import me.splleat.messengerproject.application.channel.dto.ChannelMessageCursorCommand;
import me.splleat.messengerproject.interfaces.rest.channel.dto.ChannelMessagePageResponse;

@UseCase
@RequiredArgsConstructor
public class ChannelMessageCursorGetUseCase {
    private final MessageQueryRepository messageQueryRepository;
    private final ChannelUserSettingService channelUserSettingService;

    public ChannelMessagePageResponse execute(ChannelMessageCursorCommand command) {
        channelUserSettingService.validateParticipant(command.userId(), command.channelId());

        switch (command.direction()) {
            case UP -> {
                return messageQueryRepository.findByPrevId(command.channelId(), command.cursorId());
            }

            case DOWN -> {
                return messageQueryRepository.findByNextId(command.channelId(), command.cursorId());
            }

            default -> throw new BusinessException(ErrorCode.INVALID_INPUT);
        }
    }
}
