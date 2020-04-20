package com.poc.rabbitMQ.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("event")
public class EventRest {
    @Autowired
    private EventService eventService;

    @PostMapping
    public Event save(@RequestBody Object object) {
        return eventService.save(object);
    }
}
