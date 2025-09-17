package com.example.apparend;

public class Pieza {
    private String material;
    private float largo;
    private float ancho;

    // Constructor
    public Pieza(String material, float largo, float ancho) {
        this.material = material;
        this.largo = largo;
        this.ancho = ancho;
    }

    // Getters
    public String getMaterial() {
        return material;
    }

    public float getLargo() {
        return largo;
    }

    public float getAncho() {
        return ancho;
    }
}
