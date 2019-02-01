package com.gateway;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GatewayRouter implements CommandLineRunner{

    public static void main(String[] args) {
        SpringApplication.run(GatewayRouter.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        //clean
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {

        }));
    }

}
