package com.poc.rabbitMQ.input;

import com.poc.rabbitMQ.utils.ConnectionFactoryUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

@Service
public class InputService {
    private final InputRepository inputRepository;

    public InputService(InputRepository inputRepository) {
        this.inputRepository = inputRepository;
    }

    private void validate(MultipartFile file){
        if(file.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Um arquivo deve ser informado.");
        }

        if(!Objects.equals(file.getContentType(), "application/xml")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O arquivo informado deve ser um XML.");
        }
    }

    void publishInRabbit(MultipartFile file){
        ConnectionFactory connectionFactory = ConnectionFactoryUtil.newFactory();

        validate(file);

        try(Connection connection = connectionFactory.newConnection(); Channel channel = connection.createChannel()){
            byte[] xml = file.getBytes();

            channel.exchangeDeclare("queueA", "fanout");
            channel.queueDeclare("queueA", false, false, false, null);
            channel.basicPublish("queueA", "", null, xml);

            System.out.println(String.format("[✓][Input Service]: publicado '%s'.", file.getOriginalFilename()));
        } catch(IOException | TimeoutException e){

            System.out.println(String.format("[×][Input Service]: erro ao publicar no Rabbit → '%s'.", e.getMessage()));
        }
    }

    public void save(byte[] xml){
        Input input = new Input();
        input.setXml(xml);

        inputRepository.save(input);
    }
}