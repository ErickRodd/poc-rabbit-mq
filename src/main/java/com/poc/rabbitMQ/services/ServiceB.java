package com.poc.rabbitMQ.services;

import com.poc.rabbitMQ.utils.ConnectionFactoryUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeoutException;

@Service
public class ServiceB {

    @Bean
    private void saveMsgQueueB() {
        System.out.println(String.format("[%s][♦][Service B]: Esperando por novas mensagens...", LocalTime.now()));

        ConnectionFactory factory = ConnectionFactoryUtil.newFactory();

        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.exchangeDeclare("queueB", "fanout");
            channel.queueDeclare("queueB", false, false, false, null);
            channel.queueBind("queueB", "queueB", "");

            DeliverCallback callback = (consumerTag, delivery) -> {
                String xmlString = new String(delivery.getBody(), StandardCharsets.UTF_8);
                xmlString = StringUtils.replace(xmlString, "</idade>", "</idade>\n<nacionalidade>Brasileiro</nacionalidade>");

                OutputStream outputStream = new FileOutputStream(new File(System.getProperty("user.home") + String.format("/Desktop/xml %s, %s.xml", LocalTime.now().format(DateTimeFormatter.ofPattern("HH-mm-ss")), LocalDate.now())));
                outputStream.write(xmlString.getBytes());
                outputStream.close();

                System.out.println(String.format("[%s][✔][Service B]: XML modificado consumido e salvo no disco.", LocalTime.now()));
            };

            channel.basicConsume("queueB", true, callback, consumerTag -> {});

        } catch (IOException | TimeoutException e) {
            System.out.println(String.format("[%s][✘][Service B]: erro ao consumir XML → '%s'.", LocalTime.now(), e.getMessage()));
        }
    }
}
