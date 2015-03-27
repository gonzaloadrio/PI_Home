package com.app.gonzalo.pihome.Objetos;

/**
 * Created by Gonzalo on 24/02/2015.
 * Define los atributos de un Dispositivo
 */
public class Dispositivo {
    int id;
    String zona, tipo, descripcion;

    @Override
    public String toString() {
        return "Dispositivo{" +
                "id=" + id +
                ", zona='" + zona + '\'' +
                ", tipo='" + tipo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getZona() {
        return zona;
    }

    public void setZona(String zona) {
        this.zona = zona;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Dispositivo(int id, String zona, String tipo, String descripcion) {

        this.id = id;
        this.zona = zona;
        this.tipo = tipo;
        this.descripcion = descripcion;
    }
}
