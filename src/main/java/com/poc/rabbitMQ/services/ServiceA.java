package com.poc.rabbitMQ.services;

import com.poc.rabbitMQ.utils.ConnectionFactoryUtil;
import com.poc.rabbitMQ.utils.RetryPublishUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalTime;
import java.util.Random;
import java.util.concurrent.TimeoutException;

@Service
public class ServiceA {

    @Bean
    private void consumeInputMessage() {
        System.out.println(String.format("[%s][♦][Service A]: Esperando por novas mensagens...", LocalTime.now()));

        ConnectionFactory factory = ConnectionFactoryUtil.newFactory();

        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.exchangeDeclare("queueA", "fanout");
            channel.queueDeclare("queueA", false, false, false, null);
            channel.queueBind("queueA", "queueA", "");

            DeliverCallback callback = (consumerTag, delivery) -> {
                System.out.println(String.format("[%s][✔][Service A]: XML consumido.", LocalTime.now()));

                publishToServiceB(delivery.getBody());
                publishToServiceC(delivery.getBody());
            };

            channel.basicConsume("queueA", true, callback, consumerTag -> {});

        } catch (IOException | TimeoutException e) {
            System.out.println(String.format("[%s][✘][Service A]: erro ao consumir XML → '%s'.", LocalTime.now(), e.getMessage()));
        }
    }

    private void publishToServiceB(byte[] xml) {
        ConnectionFactory factory = ConnectionFactoryUtil.newFactory();

        try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {

            if (new Random().nextBoolean()) {
                throw new IOException("Erro gerado aleatóriamente para testar o delay do reenvio de mensagens rejeitadas.");
            }

            channel.exchangeDeclare("queueB", "fanout");
            channel.queueDeclare("queueB", false, false, false, null);
            channel.basicPublish("queueB", "", null, xml);

            System.out.println(String.format("[%s][✔][Service A]: publicado XML modificado para a fila B.", LocalTime.now()));
        } catch (IOException | TimeoutException e) {
            System.out.println(String.format("[%s][✘][Service A]: erro ao publicar XML modificado → '%s'.", LocalTime.now(), e.getMessage()));

            RetryPublishUtil.retryProducer("queueB_delayed", "queueB_delayed", 30000, xml);

            System.out.println(String.format("[%s][✘][Service A]: tentando novamente em 30 segundos.", LocalTime.now()));
        }
    }

    @Bean
    private void retryPublishServiceB() {
        DeliverCallback callback = (consumerTag, delivery) -> {
            publishToServiceB(delivery.getBody());
        };

        RetryPublishUtil.retryConsumer("queueB_delayed", callback);
    }

    private void publishToServiceC(byte[] xml) {
        ConnectionFactory factory = ConnectionFactoryUtil.newFactory();

        try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
            if (new Random().nextBoolean()) {
                throw new IOException("Erro gerado aleatóriamente para testar o delay do reenvio de mensagens rejeitadas.");
            }

            channel.exchangeDeclare("queueC", "fanout");
            channel.queueDeclare("queueC", false, false, false, null);
            channel.basicPublish("queueC", "", null, xml);

            System.out.println(String.format("[%s][✔][Service A]: publicado XML para a fila C.", LocalTime.now()));
        } catch (IOException | TimeoutException e) {
            System.out.println(String.format("[%s][✘][Service A]: erro ao publicar mensagem para a fila C → '%s'.", LocalTime.now(), e.getMessage()));
            System.out.println(String.format("[%s][✘][Service A]: tentando novamente em 30 segundos.", LocalTime.now()));

            RetryPublishUtil.retryProducer("queueC_delayed", "queueC_delayed", 30000, xml);
        }
    }

    @Bean
    private void retryPublishServiceC() {
        DeliverCallback callback = (consumerTag, delivery) -> {
            publishToServiceC(delivery.getBody());
        };

        RetryPublishUtil.retryConsumer("queueC_delayed", callback);
    }
}
