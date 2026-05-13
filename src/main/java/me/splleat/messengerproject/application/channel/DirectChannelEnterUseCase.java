package me.splleat.messengerproject.application.channel;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.channel.dto.DirectChannelEnterCommand;
import me.splleat.messengerproject.common.annotation.UseCase;
import me.splleat.messengerproject.common.constant.CursorConstant;
import me.splleat.messengerproject.domain.channel.ChannelUserSetting;
import me.splleat.messengerproject.domain.channel.ChannelUserSettingService;
import me.splleat.messengerproject.infrastructure.persistence.querydsl.MessageQueryRepository;
import me.splleat.messengerproject.interfaces.websocket.message.dto.MessageCursorBothResponse;
import me.splleat.messengerproject.interfaces.websocket.message.dto.MessageResponse;
import org.springframework.data.domain.Slice;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
public class DirectChannelEnterUseCase {
    private final MessageQueryRepository messageQueryRepository;
    private final ChannelUserSettingService channelUserSettingService;

    @Transactional(readOnly = true)
    public MessageCursorBothResponse execute(DirectChannelEnterCommand command) {
        ChannelUserSetting channelUserSetting = channelUserSettingService.getChannelUserSetting(command.userId(), command.channelId());

        Long lastReadMessageId = channelUserSetting.getLastReadMessageId();

        int reqSize = CursorConstant.MESSAGE_SIZE;

        if (lastReadMessageId == null) {
            Slice<MessageResponse> messages = messageQueryRepository.findByNewest(command.channelId(), reqSize);

            return MessageCursorBothResponse.newest(messages);
        }

        Slice<MessageResponse> prev = messageQueryRepository.findByPrevId(command.channelId(), lastReadMessageId, reqSize);
        Slice<MessageResponse> next = messageQueryRepository.findByNextId(command.channelId(), lastReadMessageId, reqSize);

        return MessageCursorBothResponse.of(prev, next);
    }
}
