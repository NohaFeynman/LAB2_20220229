package com.example.lab2_20220229.models;

public class Equipo {

    public String codigo;
    public String nombre;
    public String tipo;
    public String estado;
    public String observaciones;

    public Equipo(String codigo, String nombre, String tipo, String estado, String observaciones) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.tipo = tipo;
        this.estado = estado;
        this.observaciones = observaciones;
    }
}