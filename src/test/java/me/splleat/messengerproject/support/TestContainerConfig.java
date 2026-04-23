package me.splleat.messengerproject.support;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.mysql.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestContainerConfig {

    private static final MySQLContainer MYSQL = new MySQLContainer(DockerImageName.parse("mysql:9.6.0"))
            .withDatabaseName("test_db")
            .withUsername("test")
            .withPassword("test")
            .withReuse(true);

    private static final GenericContainer<?> REDIS = new GenericContainer<>(DockerImageName.parse("redis:7.4-alpine"))
            .withExposedPorts(6379)
            .withReuse(true);

    @Bean
    MySQLContainer mySQLContainer() {
        return MYSQL;
    }

    @Bean
    GenericContainer<?> redisContainer() {
        return REDIS;
    }
}
