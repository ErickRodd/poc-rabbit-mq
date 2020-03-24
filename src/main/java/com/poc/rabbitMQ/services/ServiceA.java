package com.poc.rabbitMQ.services;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Service
public class ServiceA {

    @Bean
    public void consume(){
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.exchangeDeclare("logs", "fanout");
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, "logs", "");


            DeliverCallback callback = (consumerTag, delivery) -> {
                String msg = delivery.getEnvelope().getRoutingKey();
                System.out.println(String.format("[✓] Service A: consumido '%s'.", msg));
            };

            channel.basicConsume(queueName, true, callback, consumerTag -> {});
        } catch (IOException | TimeoutException e){
            System.out.println(String.format("[×] Service A: erro ao consumir mensagem → '%s'.", e.getMessage()));
        }
    }
}
