package me.splleat.messengerproject.support.annotation;

import me.splleat.messengerproject.support.TestContainerConfig;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@DataJpaTest
@Import(TestContainerConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public @interface ContainerDataJpaTest {
    @AliasFor(annotation = DataJpaTest.class, attribute = "properties")
    String[] properties() default {};

    @AliasFor(annotation = DataJpaTest.class, attribute = "showSql")
    boolean showSql() default true;
}
