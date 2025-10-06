package com.example.apparend.models;

import java.io.Serializable;

public class Pieza implements Serializable {
    private int id;              // ID autoincremental en BD
    private int estructura_id;   // Relación con la estructura
    private String tipoMaterial;
    private float ancho;
    private float alto;
    private float largo;
    private int cantidad;
    private String descripcion;
    private float totalM2;
    private String unidadMedida;

    // Constructor completo (cuando viene de la BD)
    public Pieza(int id, int estructura_id, String tipoMaterial,
                 float ancho, float alto, float largo,
                 int cantidad, float totalM2, String descripcion, String unidadMedida) {
        this.id = id;
        this.estructura_id = estructura_id;
        this.tipoMaterial = tipoMaterial;
        this.ancho = ancho;
        this.alto = alto;
        this.largo = largo;
        this.cantidad = cantidad;
        this.totalM2 = totalM2;
        this.descripcion = descripcion;
        this.unidadMedida = unidadMedida != null ? unidadMedida : "Metros";
    }

    // Constructor parcial (cuando la pieza aún no existe en la BD)
    public Pieza(String tipoMaterial, float ancho, float alto, float largo,
                 int cantidad, float totalM2, String descripcion) {
        this(-1, -1, tipoMaterial, ancho, alto, largo, cantidad, totalM2, descripcion, "Metros");
    }

    // Getters
    public int getId() { return id; }
    public int getEstructura_id() { return estructura_id; }
    public String getTipoMaterial() { return tipoMaterial; }
    public float getAncho() { return ancho; }
    public float getAlto() { return alto; }
    public float getLargo() { return largo; }
    public int getCantidad() { return cantidad; }
    public float getTotalM2() { return totalM2; }
    public String getDescripcion() { return descripcion; }
    public String getUnidadMedida() { return unidadMedida; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setEstructura_id(int estructura_id) { this.estructura_id = estructura_id; }
    public void setTipoMaterial(String tipoMaterial) { this.tipoMaterial = tipoMaterial; }
    public void setAncho(float ancho) { this.ancho = ancho; }
    public void setAlto(float alto) { this.alto = alto; }
    public void setLargo(float largo) { this.largo = largo; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public void setTotalM2(float totalM2) { this.totalM2 = totalM2; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setUnidadMedida(String unidadMedida) { this.unidadMedida = unidadMedida; }

    @Override
    public String toString() {
        return "Pieza{" +
                "id=" + id +
                ", estructura_id=" + estructura_id +
                ", tipoMaterial='" + tipoMaterial + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", ancho=" + ancho +
                ", alto=" + alto +
                ", largo=" + largo +
                ", cantidad=" + cantidad +
                ", totalM2=" + totalM2 +
                ", unidadMedida='" + unidadMedida + '\'' +
                '}';
    }
}