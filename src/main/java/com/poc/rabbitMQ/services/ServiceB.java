package com.poc.rabbitMQ.services;

import com.poc.rabbitMQ.utils.MessageUtil;
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
    private void consumeQueueBMessage() {
        System.out.println(String.format("[%s][♦][Service B]: Esperando por novas mensagens...", LocalTime.now()));

        DeliverCallback callback = (consumerTag, delivery) -> {
            String xmlString = new String(delivery.getBody(), StandardCharsets.UTF_8);
            xmlString = StringUtils.replace(xmlString, "</idade>", "</idade>\n<nacionalidade>Brasileiro</nacionalidade>");

            OutputStream outputStream = new FileOutputStream(new File(System.getProperty("user.home") + String.format("/Desktop/xml %s, %s.xml", LocalTime.now().format(DateTimeFormatter.ofPattern("HH-mm-ss")), LocalDate.now())));
            outputStream.write(xmlString.getBytes());
            outputStream.close();

            System.out.println(String.format("[%s][✔][Service B]: mensagem recebida consumida e salva no disco.", LocalTime.now()));
        };

        try {
            MessageUtil.consume("queueB", "fanout", "queueB", callback);
        } catch (IOException | TimeoutException e) {
            System.out.println(String.format("[%s][✘][Service B]: erro ao consumir mensagem recebida → '%s'.", LocalTime.now(), e.getCause()));
        }
    }
}
