package me.splleat.messengerproject;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class MessengerProjectApplication {

    public static void main(String[] args) {

        String nodeId = System.getenv("TSID_NODE");

        log.info("TSID 노드 ID: {}", nodeId);

        if (nodeId != null && !nodeId.isEmpty()) {
            System.setProperty("tsid.node", nodeId);
        }

        SpringApplication.run(MessengerProjectApplication.class, args);
    }
}
