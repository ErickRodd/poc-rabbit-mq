package com.poc.rabbitMQ.input;

import com.poc.rabbitMQ.utils.MessageUtil;
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

    private void validate(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Um arquivo deve ser informado.");
        }

        if (!Objects.equals(file.getContentType(), "application/xml")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O arquivo informado deve ser um XML.");
        }
    }

    void publishInRabbit(MultipartFile file) {
        validate(file);

        try {
            MessageUtil.publish("queueA", "fanout", "queueA", file.getBytes());

            System.out.println(String.format("[%s][✔][Input Service]: publicado '%s'.", LocalTime.now(), file.getOriginalFilename()));
        } catch (IOException | TimeoutException e) {
            System.out.println(String.format("[%s][✘][Input Service]: erro ao publicar no Rabbit → '%s'.", LocalTime.now(), e.getMessage()));
        }
    }

    public void save(byte[] xml) {
        Input input = new Input();
        input.setXml(xml);

        inputRepository.save(input);
    }
}