package com.poc.rabbitMQ.event;

import com.poc.rabbitMQ.status.Status;
import com.poc.rabbitMQ.status.StatusService;
import com.poc.rabbitMQ.utils.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

@Service
public class EventService {
    @Autowired
    EventRepository eventRepository;

    @Autowired
    StatusService statusService;

    public Event save(Object objType) {
        Event event = new Event() {
        };
        event.setDateCreated(LocalDateTime.now());
        event.setType(objType.getClass().getTypeName());
        event.setUuid(UUID.randomUUID().toString());

        return eventRepository.save(event);
    }

    private Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream b = new ByteArrayInputStream(bytes);
        ObjectInputStream o = new ObjectInputStream(b);

        return o.readObject();
    }

    @Bean
    private void consumeEventQueueMessages() {
        System.out.println(String.format("[%s][♦][Event Service]: Esperando por novas mensagens...", LocalTime.now()));

        try {
            MessageUtil.consume("eventExchange", "fanout", "eventQueue", (consumerTag, delivery) -> {
                Status status = statusService.initialConstruct();

                try {
                    Object object = deserialize(delivery.getBody());
                    Event event = save(object);

                    statusService.construct(status, event);
                } catch (ClassNotFoundException e) {
                    System.out.println(String.format("[%s][✘][Event Service]: erro ao converter objeto recebido → '%s'.", LocalTime.now(), e.getCause()));
                }

                statusService.finished(status);

                System.out.println(String.format("[%s][✔][Event Service]: mensagem recebida consumida.", LocalTime.now()));
            });
        } catch (IOException | TimeoutException e) {
            System.out.println(String.format("[%s][✘][Event Service]: erro ao consumir mensagem recebida → '%s'.", LocalTime.now(), e.getCause()));
        }
    }
}
