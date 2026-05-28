package me.splleat.messengerproject.application.channel;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.common.annotation.UseCase;
import me.splleat.messengerproject.common.exception.BusinessException;
import me.splleat.messengerproject.common.exception.ErrorCode;
import me.splleat.messengerproject.domain.channel.ChannelUserSetting;
import me.splleat.messengerproject.domain.channel.ChannelUserSettingService;
import me.splleat.messengerproject.infrastructure.persistence.querydsl.MessageQueryRepository;
import me.splleat.messengerproject.application.channel.dto.ChannelMessageCursorCommand;
import me.splleat.messengerproject.application.channel.dto.ChannelMessagePageResult;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
public class ChannelMessageCursorGetUseCase {
    private final MessageQueryRepository messageQueryRepository;
    private final ChannelUserSettingService channelUserSettingService;

    @Transactional
    public ChannelMessagePageResult execute(ChannelMessageCursorCommand command) {
        ChannelUserSetting setting = channelUserSettingService.getChannelUserSetting(command.userId(), command.channelId());

        switch (command.direction()) {
            case PREV -> {
                return messageQueryRepository.findByPrevId(command.channelId(), command.cursorId());
            }

            case NEXT -> {
                ChannelMessagePageResult result = messageQueryRepository.findByNextId(command.channelId(), command.cursorId());

                setting.updateLastReadMessage(result.cursorId());

                return result;
            }

            default -> throw new BusinessException(ErrorCode.INVALID_INPUT);
        }
    }
}
