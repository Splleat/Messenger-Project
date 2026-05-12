package me.splleat.messengerproject.interfaces.rest.channel.dto;

import me.splleat.messengerproject.domain.profile.UserProfile;

public record ChannelParticipantResponse(
        long userId,
        String username,
        String profileImage,
        String statusMessage
) {
    public static ChannelParticipantResponse from(UserProfile userProfile) {
        return new ChannelParticipantResponse(
                userProfile.getUserId(),
                userProfile.getName(),
                userProfile.getImageUrl(),
                userProfile.getStatusMessage()
        );
    }
}
