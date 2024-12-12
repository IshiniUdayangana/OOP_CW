package edu.iit.crm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync

public class TicketSystemApplication {

    public static void main(String[] args) {

        SpringApplication.run(TicketSystemApplication.class, args);
    }
}
