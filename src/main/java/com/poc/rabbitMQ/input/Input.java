package com.poc.rabbitMQ.input;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("mensagens")
public class Input {
    @Id
    private String id;
    private byte[] xml;

    public Input() {
    }

    public Input(String id, byte[] xml) {
        this.id = id;
        this.xml = xml;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public byte[] getXml() {
        return xml;
    }

    public void setXml(byte[] xml) {
        this.xml = xml;
    }
}
