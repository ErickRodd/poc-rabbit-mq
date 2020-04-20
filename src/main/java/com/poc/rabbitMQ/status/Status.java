package com.poc.rabbitMQ.status;

import com.poc.rabbitMQ.utils.StatusEnum;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("event-status")
public class Status {
    @Id
    private String id;
    private String eventId;
    private String eventType;
    private StatusEnum status;
    private LocalDateTime dateCreated;
    private LocalDateTime dateReceived;
    private LocalDateTime dateStartedLastExecution;
    private LocalDateTime dateFinished;
    private String hostnameReceived;
    private String hostnameExecution;

    public Status() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public StatusEnum getStatus() {
        return status;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public LocalDateTime getDateReceived() {
        return dateReceived;
    }

    public void setDateReceived(LocalDateTime dateReceived) {
        this.dateReceived = dateReceived;
    }

    public LocalDateTime getDateStartedLastExecution() {
        return dateStartedLastExecution;
    }

    public void setDateStartedLastExecution(LocalDateTime dateStartedLastExecution) {
        this.dateStartedLastExecution = dateStartedLastExecution;
    }

    public LocalDateTime getDateFinished() {
        return dateFinished;
    }

    public void setDateFinished(LocalDateTime dateFinished) {
        this.dateFinished = dateFinished;
    }

    public String getHostnameReceived() {
        return hostnameReceived;
    }

    public void setHostnameReceived(String hostnameReceived) {
        this.hostnameReceived = hostnameReceived;
    }

    public String getHostnameExecution() {
        return hostnameExecution;
    }

    public void setHostnameExecution(String hostnameExecution) {
        this.hostnameExecution = hostnameExecution;
    }
}
