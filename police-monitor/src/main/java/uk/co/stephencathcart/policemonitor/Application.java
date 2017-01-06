package uk.co.stephencathcart.policemonitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

/**
 * Main Spring Boot class which enables JMS and listens for messages. We persist
 * these messages to Azure Table Storage.
 *
 * @author Stephen Cathcart
 */
@SpringBootApplication
@EnableJms
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
