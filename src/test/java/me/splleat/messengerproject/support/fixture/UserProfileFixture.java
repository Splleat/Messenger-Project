package me.splleat.messengerproject.support.fixture;

import me.splleat.messengerproject.domain.user.UserProfile;

public final class UserProfileFixture {
    public static final String NAME = "testUser";

    public static UserProfile defaultUserProfile(Long userId) {
        return UserProfile.create(
                userId,
                NAME
        );
    }

}
