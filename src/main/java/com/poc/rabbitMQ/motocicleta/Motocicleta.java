package com.poc.rabbitMQ.motocicleta;

import com.poc.rabbitMQ.event.Event;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Document("motocicletas")
public class Motocicleta extends Event implements Serializable {
    private String marca;
    private String modelo;
    private String ano;
    private String cor;
    private String placa;

    public Motocicleta() {
    }

    public Motocicleta(String marca, String modelo, String ano, String cor, String placa) {
        this.marca = marca;
        this.modelo = modelo;
        this.ano = ano;
        this.cor = cor;
        this.placa = placa;
    }


    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }
}
