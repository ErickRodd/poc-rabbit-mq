package com.poc.rabbitMQ.input;

import com.poc.rabbitMQ.utils.MessageUtil;
import com.poc.rabbitMQ.utils.RetryMessageUtil;
import com.poc.rabbitMQ.utils.ThrowExceptionUtil;
import com.rabbitmq.client.DeliverCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalTime;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

@Service
public class InputService {
    private final InputRepository inputRepository;

    public InputService(InputRepository inputRepository) {
        this.inputRepository = inputRepository;
    }

    void validate(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Um arquivo deve ser informado.");
        }

        if (!Objects.equals(file.getContentType(), "application/xml")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O arquivo informado deve ser um XML.");
        }
    }

    void publishToServiceA(byte[] file) {
        try {
            ThrowExceptionUtil.randomIOException();

            MessageUtil.publish("queueA", "fanout", "queueA", file);

            System.out.println(String.format("[%s][✔][Input Service]: publicado mensagem com o arquivo para a fila da service A.", LocalTime.now()));
        } catch (IOException | TimeoutException e) {
            System.out.println(String.format("[%s][✘][Input Service]: erro ao publicar para a fila da service A → '%s'.", LocalTime.now(), e.getCause()));

            RetryMessageUtil.retryProducer("queueA_delayed", "queueA_delayed", 30000, file);

            System.out.println(String.format("[%s][✘][Input Service]: tentando novamente em 30 segundos.", LocalTime.now()));
        }
    }

    @Bean
    private void retryPublishToServiceA(){
        DeliverCallback callback = (consumerTag, delivery) -> {
            publishToServiceA(delivery.getBody());
        };

        RetryMessageUtil.retryConsumer("queueA_delayed", callback);
    }

    public void save(byte[] xml) {
        Input input = new Input();
        input.setXml(xml);

        inputRepository.save(input);
    }
}