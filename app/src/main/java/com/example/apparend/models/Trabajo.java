package com.example.apparend.models;

import java.io.Serializable;

public class Trabajo implements Serializable {
    private int id;
    private String cliente;
    private String descripcion;
    private String fechaHora;

    public Trabajo(int id, String cliente, String descripcion, String fechaHora) {
        this.id = id;
        this.cliente = cliente;
        this.descripcion = descripcion;
        this.fechaHora = fechaHora;
    }

    // Getters
    public int getId() { return id; }
    public String getCliente() { return cliente; }
    public String getDescripcion() { return descripcion; }
    public String getFechaHora() { return fechaHora; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setCliente(String cliente) { this.cliente = cliente; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setFechaHora(String fechaHora) { this.fechaHora = fechaHora; }
}
