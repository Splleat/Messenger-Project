package me.splleat.messengerproject.support.annotation;

import me.splleat.messengerproject.support.TestCacheConfig;
import me.splleat.messengerproject.support.TestContainerConfig;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({TestContainerConfig.class, TestCacheConfig.class})
@SpringBootTest
public @interface IntegrationTest {
    @AliasFor(attribute = "properties", annotation = SpringBootTest.class)
    String[] value() default {};
}
