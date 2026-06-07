package me.splleat.messengerproject;

import me.splleat.messengerproject.support.TestContainerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestContainerConfig.class)
class MessengerProjectApplicationTests {

    @Test
    void contextLoads() {
    }

}
