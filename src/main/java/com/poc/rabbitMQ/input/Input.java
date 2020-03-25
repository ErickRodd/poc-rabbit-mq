package com.poc.rabbitMQ.input;

import javax.persistence.*;

@Entity
@Table(name = "mensagens")
public class Input {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "xml")
    private byte[] xml;

    public Input() {
    }

    public Input(Long id, byte[] xml) {
        this.id = id;
        this.xml = xml;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getXml() {
        return xml;
    }

    public void setXml(byte[] xml) {
        this.xml = xml;
    }

}
