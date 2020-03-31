package com.poc.rabbitMQ.utils;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MessageUtil {
    public static void publish(String exchangeName, String exchangeType, String queueName, byte[] messsage) throws IOException, TimeoutException {
        ConnectionFactory factory = ConnectionFactoryUtil.newFactory();

        try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(exchangeName, exchangeType);
            channel.queueDeclare(queueName, false, false, false, null);
            channel.basicPublish(exchangeName, "", null, messsage);
        }
    }

    public static void consume(String exchangeName, String exchangeType, String queueName, DeliverCallback callback) throws IOException, TimeoutException {
        Connection connection = ConnectionFactoryUtil.newFactory().newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(exchangeName, exchangeType);
        channel.queueDeclare(queueName, false, false, false, null);
        channel.queueBind(queueName, exchangeName, "");

        channel.basicConsume(queueName, true, callback, consumerTag -> {});
    }
}
