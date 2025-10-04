package com.example.apparend.models;

import com.example.apparend.models.Pieza;
import java.io.Serializable;
import java.util.List;

public class Estructura implements Serializable {
    private int id;
    private int trabajoId;
    private String descripcion;
    private String imagenUri;
    private float totalM2;

    // ✅ Nueva lista de piezas
    private List<Pieza> listaPiezas;

    public Estructura(int id, int trabajoId, String descripcion, String imagenUri, float totalM2) {
        this.id = id;
        this.trabajoId = trabajoId;
        this.descripcion = descripcion;
        this.imagenUri = imagenUri;
        this.totalM2 = totalM2;
    }

    // Getters y setters
    public int getId() { return id; }
    public int getTrabajoId() { return trabajoId; }
    public String getDescripcion() { return descripcion; }
    public String getImagenUri() { return imagenUri; }
    public float getTotalM2() { return totalM2; }

    // ✅ Getters y setters de listaPiezas
    public List<Pieza> getListaPiezas() {
        return listaPiezas;
    }

    public void setListaPiezas(List<Pieza> listaPiezas) {
        this.listaPiezas = listaPiezas;
    }

    public Estructura(int id, int trabajoId, String descripcion, String imagenUri, float totalM2, List<Pieza> listaPiezas) {
        this.id = id;
        this.trabajoId = trabajoId;
        this.descripcion = descripcion;
        this.imagenUri = imagenUri;
        this.totalM2 = totalM2;
        this.listaPiezas = listaPiezas;
    }



}
