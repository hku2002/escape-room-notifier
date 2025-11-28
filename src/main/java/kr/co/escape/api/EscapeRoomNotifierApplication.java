package kr.co.escape.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class EscapeRoomNotifierApplication {

    public static void main(String[] args) {
        SpringApplication.run(EscapeRoomNotifierApplication.class, args);
    }

}
