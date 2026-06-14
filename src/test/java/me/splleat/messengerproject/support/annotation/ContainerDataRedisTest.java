package me.splleat.messengerproject.support.annotation;

import me.splleat.messengerproject.support.TestContainerConfig;
import org.springframework.boot.data.redis.test.autoconfigure.DataRedisTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@DataRedisTest
@Import(TestContainerConfig.class)
public @interface ContainerDataRedisTest {
    @AliasFor(annotation = DataRedisTest.class, attribute = "properties")
    String[] properties() default {};
}
