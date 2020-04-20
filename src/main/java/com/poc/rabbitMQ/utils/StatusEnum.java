package com.poc.rabbitMQ.utils;

public enum StatusEnum {
    received("Received"),
    processing("Processing"),
    finished("Finished");

    private String description;

    StatusEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
