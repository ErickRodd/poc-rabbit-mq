package com.poc.rabbitMQ.services;

import com.poc.rabbitMQ.input.InputService;
import com.poc.rabbitMQ.utils.ConnectionFactoryUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Service
public class ServiceC {
    private final InputService inputService;

    public ServiceC(InputService inputService) {
        this.inputService = inputService;
    }

    @Bean
    private void saveMsgInput() {
        System.out.println("[•][Service C]: Esperando por novas mensagens...");

        ConnectionFactory factory = ConnectionFactoryUtil.newFactory();

        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.exchangeDeclare("queueC", "fanout");
            channel.queueDeclare("queueC", false, false, false, null);
            channel.queueBind("queueC", "queueC", "");

            DeliverCallback callback = (consumerTag, delivery) -> {
                inputService.save(delivery.getBody());

                System.out.println("[✓][Service C]: XML consumido e salvo no banco.");
            };

            channel.basicConsume("queueC", true, callback, consumerTag -> {
            });

        } catch (IOException | TimeoutException e) {
            System.out.println(String.format("[×][Service C]: erro ao consumir XML → '%s'.", e.getMessage()));
        }
    }
}
