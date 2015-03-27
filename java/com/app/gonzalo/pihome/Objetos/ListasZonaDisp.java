package com.app.gonzalo.pihome.Objetos;

import java.util.ArrayList;

/**
 * Created by Gonzalo on 25/02/2015.
 * Define dos listas, una de Dispositivos y otra de Zonas
 */
public class ListasZonaDisp {
    ArrayList<Dispositivo> listaDisp;
    ArrayList<String>listaZonas;

    public ListasZonaDisp(ArrayList<Dispositivo> listaDisp, ArrayList<String> listaZonas) {
        this.listaDisp = listaDisp;
        this.listaZonas = listaZonas;
    }

    public ArrayList<Dispositivo> getListaDisp() {
        return listaDisp;
    }

    public void setListaDisp(ArrayList<Dispositivo> listaDisp) {
        this.listaDisp = listaDisp;
    }

    public ArrayList<String> getListaZonas() {
        return listaZonas;
    }

    public void setListaZonas(ArrayList<String> listaZonas) {
        this.listaZonas = listaZonas;
    }
}
