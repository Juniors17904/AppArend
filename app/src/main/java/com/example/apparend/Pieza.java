package com.example.apparend;

public class Pieza {
    private String tipoMaterial;
    private String dimensiones;
    private int cantidad;
    private float totalM2;

    public Pieza(String tipoMaterial, String dimensiones, int cantidad, float totalM2) {
        this.tipoMaterial = tipoMaterial;
        this.dimensiones = dimensiones;
        this.cantidad = cantidad;
        this.totalM2 = totalM2;
    }

    // Getters
    public String getTipoMaterial() { return tipoMaterial; }
    public String getDimensiones() { return dimensiones; }
    public int getCantidad() { return cantidad; }
    public float getTotalM2() { return totalM2; }

    // Setters (opcionales)
    public void setTipoMaterial(String tipoMaterial) { this.tipoMaterial = tipoMaterial; }
    public void setDimensiones(String dimensiones) { this.dimensiones = dimensiones; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public void setTotalM2(float totalM2) { this.totalM2 = totalM2; }
}