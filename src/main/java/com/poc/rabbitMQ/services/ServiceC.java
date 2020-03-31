package com.poc.rabbitMQ.services;

import com.poc.rabbitMQ.input.InputService;
import com.poc.rabbitMQ.utils.MessageUtil;
import com.rabbitmq.client.DeliverCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalTime;
import java.util.concurrent.TimeoutException;

@Service
public class ServiceC {
    private final InputService inputService;

    private ServiceC(InputService inputService) {
        this.inputService = inputService;
    }

    @Bean
    private void saveMsgInput() {
        System.out.println(String.format("[%s][♦][Service C]: Esperando por novas mensagens...", LocalTime.now()));

        DeliverCallback callback = (consumerTag, delivery) -> {
            inputService.save(delivery.getBody());

            System.out.println(String.format("[%s][✔][Service C]: XML consumido e salvo no banco.", LocalTime.now()));
        };

        try {
            MessageUtil.consume("queueC", "fanout", "queueC", callback);
        } catch (IOException | TimeoutException e) {
            System.out.println(String.format("[%s][✘][Service C]: erro ao consumir XML → '%s'.", LocalTime.now(), e.getMessage()));
        }
    }
}
