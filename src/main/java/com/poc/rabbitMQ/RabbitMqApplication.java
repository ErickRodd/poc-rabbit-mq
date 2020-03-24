package com.poc.rabbitMQ;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RabbitMqApplication {

    public static void main(String[] args) {
        SpringApplication.run(RabbitMqApplication.class, args);
        System.out.println("[•] Service 1: Esperando por novas mensagens...");
        System.out.println("[•] Service 2: Esperando por novas mensagens...");
        System.out.println("[•] Service 3: Esperando por novas mensagens...");
    }

}
