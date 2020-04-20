package com.poc.rabbitMQ.motocicleta;

import com.poc.rabbitMQ.utils.MessageUtil;
import com.poc.rabbitMQ.utils.RetryMessageUtil;
import com.rabbitmq.client.DeliverCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.time.LocalTime;
import java.util.concurrent.TimeoutException;

@Service
public class MotocicletaService {
    @Autowired
    private MotocicletaRepository motocicletaRepository;

    public Motocicleta save(Motocicleta motinha) {
        return motocicletaRepository.save(motinha);
    }

    private byte[] serialize(Object object) throws IOException {
        try (ByteArrayOutputStream b = new ByteArrayOutputStream()) {
            try (ObjectOutputStream o = new ObjectOutputStream(b)) {
                o.writeObject(object);
            }
            return b.toByteArray();
        }
    }

    void publishToQueue(Object object) throws IOException {
        try {
            MessageUtil.publish("eventExchange", "fanout", "eventQueue", serialize(object));

            System.out.println(String.format("[%s][✔][Event Service]: publicado mensagem para a fila.", LocalTime.now()));
        } catch (IOException | TimeoutException e) {
            System.out.println(String.format("[%s][✘][Event Service]: erro ao publicar para a fila → '%s'.", LocalTime.now(), e.getMessage()));

            RetryMessageUtil.retryProducer("eventExchange_delayed", "eventQueue_delayed", 30000, serialize(object));

            System.out.println(String.format("[%s][✘][Event Service]: tentando novamente em 30 segundos.", LocalTime.now()));
        }
    }

    @Bean
    private void retryPublishToQueue() {
        DeliverCallback callback = (consumerTag, delivery) -> {
            publishToQueue(delivery.getBody());
        };

        RetryMessageUtil.retryConsumer("eventQueue_delayed", callback);
    }
}
