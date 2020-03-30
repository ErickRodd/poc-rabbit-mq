package com.poc.rabbitMQ.services;

import com.poc.rabbitMQ.utils.ConnectionFactoryUtil;
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
public class ServiceB {

    @Bean
    private void saveMsgQueueB() {
        System.out.println("[•][Service B]: Esperando por novas mensagens...");

        ConnectionFactory factory = ConnectionFactoryUtil.newFactory();

        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.exchangeDeclare("queueB", "fanout");
            channel.queueDeclare("queueB", false, false, false, null);
            channel.queueBind("queueB", "queueB", "");

            DeliverCallback callback = (consumerTag, delivery) -> {
                System.out.println("[✓][Service B]: XML modificado consumido e salvo no disco.");

                OutputStream outputStream = new FileOutputStream(new File(System.getProperty("user.home") + String.format("/Desktop/xmlMod-%s.xml", LocalDate.now())));
                outputStream.write(delivery.getBody());
                outputStream.close();
            };

            channel.basicConsume("queueB", true, callback, consumerTag -> {
            });

        } catch (IOException | TimeoutException e) {
            System.out.println(String.format("[×][Service B]: erro ao consumir XML → '%s'.", e.getMessage()));
        }
    }
}
