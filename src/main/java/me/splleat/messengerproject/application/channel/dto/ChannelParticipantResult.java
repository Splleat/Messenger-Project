package me.splleat.messengerproject.application.channel.dto;

import me.splleat.messengerproject.domain.profile.UserProfile;

public record ChannelParticipantResult(
        long userId,
        String username,
        String profileImage,
        String statusMessage
) {
    public static ChannelParticipantResult from(UserProfile userProfile) {
        return new ChannelParticipantResult(
                userProfile.getUserId(),
                userProfile.getName(),
                userProfile.getImageUrl(),
                userProfile.getStatusMessage()
        );
    }
}
