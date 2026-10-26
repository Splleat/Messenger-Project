package me.splleat.messengerproject.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

@Configuration(proxyBeanMethods = false)
public class PathMatcherConfig {

    @Bean
    public PathMatcher pathMatcher() {
        return new AntPathMatcher();
    }
}
