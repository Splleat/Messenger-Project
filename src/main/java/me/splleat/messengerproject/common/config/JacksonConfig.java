package me.splleat.messengerproject.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.ext.javatime.deser.LocalDateTimeDeserializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateTimeSerializer;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration(proxyBeanMethods = false)
public class JacksonConfig {
    @Bean
    JsonMapper jsonMapper() {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        return JsonMapper.builder()
                .addModule(new SimpleModule()
                        .addSerializer(Long.class, ToStringSerializer.instance)
                        .addSerializer(Long.TYPE, ToStringSerializer.instance)
                        .addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(formatter))
                        .addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(formatter)))
                .findAndAddModules()
                .build();
    }
}
