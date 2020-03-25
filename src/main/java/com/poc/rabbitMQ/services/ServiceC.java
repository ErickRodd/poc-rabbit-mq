package com.poc.rabbitMQ.services;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.concurrent.TimeoutException;

@Service
public class ServiceC {

    @Bean
    private void saveMsgQueueB() {
        System.out.println("[•][Service C]: Esperando por novas mensagens...");

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.exchangeDeclare("queueB", "fanout");
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, "queueB", "");

            DeliverCallback callback = (consumerTag, delivery) -> {
                System.out.println("[✓][Service C]: XML modificado consumido e salvo no disco.");

                OutputStream outputStream = new FileOutputStream(new File(System.getProperty("user.home") + String.format("/Desktop/xmlMod-%s.xml", LocalDate.now())));
                outputStream.write(delivery.getBody());
                outputStream.close();
            };

            channel.basicConsume(queueName, true, callback, consumerTag -> {
            });

        } catch (IOException | TimeoutException e) {
            System.out.println(String.format("[×][Service C]: erro ao consumir XML → '%s'.", e.getMessage()));
        }
    }
}
