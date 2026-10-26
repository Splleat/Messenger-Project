package me.splleat.messengerproject.common.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties("security")
public record SecurityProperties(
        List<String> publicPaths
) {
    public SecurityProperties {
        publicPaths = (publicPaths == null) ? List.of() : List.copyOf(publicPaths);
    }
}
