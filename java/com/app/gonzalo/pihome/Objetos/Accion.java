package com.app.gonzalo.pihome.Objetos;

/**
 * Created by Gonzalo on 28/02/2015.
 * Clase Accion define un objeto que indica la accion y un objeto Dispositivo
 */
public class Accion {
    boolean accion;
    Dispositivo dispositivo;

    public Accion(boolean accion, Dispositivo dispositivo) {
        this.accion = accion;
        this.dispositivo = dispositivo;
    }

    public boolean getAccion() {
        return accion;
    }

    public void setAccion(boolean accion) {
        this.accion = accion;
    }

    public Dispositivo getDispositivo() {
        return dispositivo;
    }

    public void setDispositivo(Dispositivo dispositivo) {
        this.dispositivo = dispositivo;
    }
}
