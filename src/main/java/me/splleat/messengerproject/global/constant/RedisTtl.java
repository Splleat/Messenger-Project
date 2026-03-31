package me.splleat.messengerproject.global.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Duration;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RedisTtl {
    public static final Duration REFRESH_TOKEN = Duration.ofDays(14);
}
