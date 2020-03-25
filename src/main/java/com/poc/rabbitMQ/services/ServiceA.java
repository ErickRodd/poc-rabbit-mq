package com.poc.rabbitMQ.services;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

@Service
public class ServiceA {

    @Bean
    private void consumeInputMessage() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.exchangeDeclare("queueA", "fanout");
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, "queueA", "");

            DeliverCallback callback = (consumerTag, delivery) -> {
                String msg = delivery.getEnvelope().getRoutingKey();
                System.out.println(String.format("[✓][Service A]: consumido '%s'.", msg));

                publishToServiceB(delivery.getBody());
            };

            channel.basicConsume(queueName, true, callback, consumerTag -> {
            });

        } catch (IOException | TimeoutException e) {
            System.out.println(String.format("[×][Service A]: erro ao consumir XML → '%s'.", e.getMessage()));
        }
    }

    private void publishToServiceB(byte[] xml) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        String xmlString = new String(xml, StandardCharsets.UTF_8);

        try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
            xmlString = StringUtils.replace(xmlString, "</idade>", "</idade>\n<nacionalidade>Brasileiro</nacionalidade>");

            channel.exchangeDeclare("queueB", "fanout");
            channel.basicPublish("queueB", "", null, xmlString.getBytes());

            System.out.println("[✓][Service A]: publicado XML modificado para a fila B.");
        } catch (IOException | TimeoutException e) {
            System.out.println(String.format("[×][Service A]: erro ao publicar XML modificado → '%s'.", e.getMessage()));
        }
    }
}
