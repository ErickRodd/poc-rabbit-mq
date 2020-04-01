package com.poc.rabbitMQ.input;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("emissao")
public class InputRest {
    private final InputService inputService;

    @Autowired
    public InputRest(InputService inputService) {
        this.inputService = inputService;
    }

    @PostMapping
    private void saveMessage(@RequestParam("file") MultipartFile file) throws IOException {
        inputService.validate(file);
        inputService.publishInRabbit(file.getBytes());
    }
}
