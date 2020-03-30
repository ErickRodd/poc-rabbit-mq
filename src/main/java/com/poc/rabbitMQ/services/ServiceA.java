package com.poc.rabbitMQ.services;

import com.poc.rabbitMQ.utils.ConnectionFactoryUtil;
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
        System.out.println("[•][Service A]: Esperando por novas mensagens...");

        ConnectionFactory factory = ConnectionFactoryUtil.newFactory();

        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.exchangeDeclare("queueA", "fanout");
            channel.queueDeclare("queueA", false, false, false, null);
            channel.queueBind("queueA", "queueA", "");

            DeliverCallback callback = (consumerTag, delivery) -> {
                System.out.println("[✓][Service A]: XML consumido.");

                publishToServiceB(delivery.getBody());
                publishToServiceC(delivery.getBody());
            };

            channel.basicConsume("queueA", true, callback, consumerTag -> {
            });

        } catch (IOException | TimeoutException e) {
            System.out.println(String.format("[×][Service A]: erro ao consumir XML → '%s'.", e.getMessage()));
        }
    }

    private void publishToServiceB(byte[] xml) {
        ConnectionFactory factory = ConnectionFactoryUtil.newFactory();

        String xmlString = new String(xml, StandardCharsets.UTF_8);

        try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
            xmlString = StringUtils.replace(xmlString, "</idade>", "</idade>\n<nacionalidade>Brasileiro</nacionalidade>");

            channel.exchangeDeclare("queueB", "fanout");
            channel.queueDeclare("queueB", false, false, false, null);
            channel.basicPublish("queueB", "", null, xmlString.getBytes());

            System.out.println("[✓][Service A]: publicado XML modificado para a fila B.");
        } catch (IOException | TimeoutException e) {
            System.out.println(String.format("[×][Service A]: erro ao publicar XML modificado → '%s'.", e.getMessage()));
        }
    }

    private void publishToServiceC(byte[] xml) {
        ConnectionFactory factory = ConnectionFactoryUtil.newFactory();

        try(Connection connection = factory.newConnection(); Channel channel = connection.createChannel()){
            channel.exchangeDeclare("queueC", "fanout");
            channel.queueDeclare("queueC", false, false, false, null);
            channel.basicPublish("queueC", "", null, xml);

            System.out.println("[✓][Service A]: publicado XML para a fila C.");
        } catch (IOException | TimeoutException e){
            System.out.println(String.format("[×][Service A]: erro ao publicar XML para a fila C → '%s'.", e.getMessage()));
        }
    }
}
