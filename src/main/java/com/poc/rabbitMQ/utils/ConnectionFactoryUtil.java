package com.poc.rabbitMQ.utils;

import com.rabbitmq.client.ConnectionFactory;

public class ConnectionFactoryUtil {

    public static ConnectionFactory newFactory(){
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("admin");
        factory.setPassword("admin");

        return factory;
    }
}
