package me.splleat.messengerproject.application.profile.dto;

public record UserProfileDetailResult(
        String email,
        String name,
        String statusMessage,
        String imageUrl
) {
    public UserProfileDetailResult withAbsoluteUrl(String absoluteUrl) {
        return new UserProfileDetailResult(
                email,
                name,
                statusMessage,
                absoluteUrl
        );
    }
}
