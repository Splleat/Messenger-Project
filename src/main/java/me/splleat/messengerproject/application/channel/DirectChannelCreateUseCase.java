package me.splleat.messengerproject.application.channel;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.channel.dto.DirectChannelCreateCommand;
import me.splleat.messengerproject.common.annotation.UseCase;
import me.splleat.messengerproject.domain.channel.Channel;
import me.splleat.messengerproject.domain.channel.ChannelService;
import me.splleat.messengerproject.domain.channel.ChannelUserSetting;
import me.splleat.messengerproject.domain.channel.ChannelUserSettingService;
import me.splleat.messengerproject.domain.user.User;
import me.splleat.messengerproject.domain.user.UserService;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
public class DirectChannelCreateUseCase {
    private final UserService userService;
    private final ChannelService channelService;
    private final ChannelUserSettingService channelUserSettingService;

    @Transactional
    public void execute(DirectChannelCreateCommand command) {
        User user = userService.getUser(command.userId());
        Channel channel = Channel.create(null, command.channelName(), command.type());
        Channel created = channelService.register(channel);
        ChannelUserSetting setting = ChannelUserSetting.create(user, created);

        channelUserSettingService.register(setting);
    }
}
