package me.splleat.messengerproject.support.fixture;

import me.splleat.messengerproject.domain.space.Space;

public final class SpaceFixture {
    public static Space defaultSpace() {
        return Space.create("testSpace");
    }
}
