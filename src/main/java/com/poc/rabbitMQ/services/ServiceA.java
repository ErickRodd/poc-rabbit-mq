package com.poc.rabbitMQ.services;

import com.poc.rabbitMQ.utils.MessageUtil;
import com.poc.rabbitMQ.utils.RetryMessageUtil;
import com.poc.rabbitMQ.utils.ThrowExceptionUtil;
import com.rabbitmq.client.DeliverCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalTime;
import java.util.concurrent.TimeoutException;

@Service
public class ServiceA {

    @Bean
    private void consumeQueueAMessage() {
        System.out.println(String.format("[%s][♦][Service A]: Esperando por novas mensagens...", LocalTime.now()));

        DeliverCallback callback = (consumerTag, delivery) -> {
            System.out.println(String.format("[%s][✔][Service A]: mensagem recebida consumida.", LocalTime.now()));

            publishToServiceB(delivery.getBody());
            publishToServiceC(delivery.getBody());
        };

        try {
            MessageUtil.consume("queueA", "fanout", "queueA", callback);
        } catch (IOException | TimeoutException e) {
            System.out.println(String.format("[%s][✘][Service A]: erro ao consumir mensagem recebida → '%s'.", LocalTime.now(), e.getCause()));
        }
    }

    private void publishToServiceB(byte[] xml) {
        try {
            //ThrowExceptionUtil.randomIOException();

            MessageUtil.publish("queueB", "fanout", "queueB", xml);

            System.out.println(String.format("[%s][✔][Service A]: publicado mensagem modificada para a fila da service B.", LocalTime.now()));
        } catch (IOException | TimeoutException e) {
            System.out.println(String.format("[%s][✘][Service A]: erro ao publicar mensagem modificada para a fila da service B → '%s'.", LocalTime.now(), e.getCause()));

            RetryMessageUtil.retryProducer("queueB_delayed", "queueB_delayed", 30000, xml);

            System.out.println(String.format("[%s][✘][Service A]: tentando novamente em 30 segundos.", LocalTime.now()));
        }
    }

    @Bean
    private void retryPublishServiceB() {
        DeliverCallback callback = (consumerTag, delivery) -> {
            publishToServiceB(delivery.getBody());
        };

        RetryMessageUtil.retryConsumer("queueB_delayed", callback);
    }

    private void publishToServiceC(byte[] xml) {
        try {
            //ThrowExceptionUtil.randomIOException();

            MessageUtil.publish("queueC", "fanout", "queueC", xml);

            System.out.println(String.format("[%s][✔][Service A]: publicado mensagem para a fila da service C.", LocalTime.now()));
        } catch (IOException | TimeoutException e) {
            System.out.println(String.format("[%s][✘][Service A]: erro ao publicar mensagem para a fila da service C → '%s'.", LocalTime.now(), e.getCause()));
            System.out.println(String.format("[%s][✘][Service A]: tentando novamente em 30 segundos.", LocalTime.now()));

            RetryMessageUtil.retryProducer("queueC_delayed", "queueC_delayed", 30000, xml);
        }
    }

    @Bean
    private void retryPublishServiceC() {
        DeliverCallback callback = (consumerTag, delivery) -> {
            publishToServiceC(delivery.getBody());
        };

        RetryMessageUtil.retryConsumer("queueC_delayed", callback);
    }
}
