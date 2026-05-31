package me.splleat.messengerproject.support.fixture;

import me.splleat.messengerproject.domain.group.Group;

public final class GroupFixture {
    public static Group defaultGroup() {
        return Group.create("testGroup");
    }
}
