package com.poc.rabbitMQ.motocicleta;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("motocicleta")
public class MotocicletaRest {
    @Autowired
    private MotocicletaService motocicletaService;

    @PostMapping("/save")
    public Motocicleta save(@RequestBody Motocicleta moto) throws IOException {
        motocicletaService.publishToQueue(moto);
        return motocicletaService.save(moto);
    }
}
