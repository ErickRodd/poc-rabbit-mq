package com.poc.rabbitMQ.utils;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class RetryMessageUtil {

    public static void retryProducer(String exchangeName, String queueName, int delayTime, byte[] message) {
        try {
            Connection connection = ConnectionFactoryUtil.newFactory().newConnection();
            Channel channel = connection.createChannel();

            Map<String, Object> args = new HashMap<>();
            args.put("x-delayed-type", "direct");
            channel.exchangeDeclare(exchangeName, "x-delayed-message", true, false, args);
            channel.queueDeclare(queueName, true, false, false, null);
            channel.queueBind(queueName, exchangeName, "");

            Map<String, Object> headers = new HashMap<>();
            headers.put("x-delay", delayTime);
            AMQP.BasicProperties.Builder props = new AMQP.BasicProperties.Builder().headers(headers);

            channel.basicPublish(exchangeName, "", props.build(), message);
        } catch (IOException | TimeoutException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void retryConsumer(String queueName, DeliverCallback callback) {
        try {
            Connection connection = ConnectionFactoryUtil.newFactory().newConnection();
            Channel channel = connection.createChannel();

            channel.queueDeclare(queueName, true, false, false, null);

            channel.basicConsume(queueName, true, callback, consumerTag -> {});
        } catch (IOException | TimeoutException e) {
            System.out.println(e.getMessage());
        }
    }
}
