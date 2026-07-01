package me.splleat.messengerproject.application.channel.dto;


public record ChannelParticipantResult(
        long userId,
        String username,
        String profileImage,
        String statusMessage
) {
    public ChannelParticipantResult withImageUrl(String absoluteUrl) {
        return new ChannelParticipantResult(
                userId,
                username,
                absoluteUrl,
                statusMessage
        );
    }
}
