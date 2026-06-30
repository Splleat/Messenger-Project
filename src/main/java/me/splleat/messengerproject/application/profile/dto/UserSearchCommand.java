package me.splleat.messengerproject.application.profile.dto;

public record UserSearchCommand(
        long excludeUserId,
        String name,
        int page
) {
    public static UserSearchCommand of(long excludeUserId, String name, int page) {
        return new UserSearchCommand(excludeUserId, name, page);
    }
}
