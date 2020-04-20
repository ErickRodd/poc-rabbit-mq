package com.poc.rabbitMQ.status;

import com.poc.rabbitMQ.event.Event;
import com.poc.rabbitMQ.utils.StatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;

@Service
public class StatusService {
    @Autowired
    private StatusRepository statusRepository;

    public Status save(Status status) {
        return statusRepository.save(status);
    }

    public Status initialConstruct() throws UnknownHostException {
        Status status = new Status();
        status.setStatus(StatusEnum.received);
        status.setHostnameReceived(InetAddress.getLocalHost().getHostName());

        return save(status);
    }

    public void construct(Status status, Event event) throws UnknownHostException {
        status.setEventId(event.getId());
        status.setEventType(event.getType());
        status.setDateCreated(event.getDateCreated());
        status.setDateReceived(LocalDateTime.now());
        status.setStatus(StatusEnum.processing);
        status.setHostnameExecution(InetAddress.getLocalHost().getHostName());

        save(status);
    }

    public void finished(Status status) {
        status.setStatus(StatusEnum.finished);
        status.setDateStartedLastExecution(LocalDateTime.now());
        status.setDateFinished(LocalDateTime.now());

        save(status);
    }
}
