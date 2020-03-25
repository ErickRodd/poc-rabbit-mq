package com.poc.rabbitMQ.services;

import com.poc.rabbitMQ.input.InputService;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Service
public class ServiceB {
    private final InputService inputService;

    public ServiceB(InputService inputService) {
        this.inputService = inputService;
    }

    @Bean
    private void saveMsgInput() {
        System.out.println("[•][Service B]: Esperando por novas mensagens...");

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.exchangeDeclare("queueA", "fanout");
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, "queueA", "");

            DeliverCallback callback = (consumerTag, delivery) -> {
                inputService.save(delivery.getBody());

                System.out.println("[✓][Service B]: XML consumido e salvo no banco.");
            };

            channel.basicConsume(queueName, true, callback, consumerTag -> {
            });

        } catch (IOException | TimeoutException e) {
            System.out.println(String.format("[×][Service B]: erro ao consumir XML → '%s'.", e.getMessage()));
        }
    }
}
