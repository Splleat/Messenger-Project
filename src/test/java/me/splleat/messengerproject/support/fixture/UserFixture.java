package me.splleat.messengerproject.support.fixture;

import me.splleat.messengerproject.domain.user.User;

public final class UserFixture {
    public static final String PASSWORD_HASH = "encryptedPassword";

    public static User defaultUser() {
        return User.create(
                "test@test.com",
                PASSWORD_HASH,
                false
        );
    }
}
