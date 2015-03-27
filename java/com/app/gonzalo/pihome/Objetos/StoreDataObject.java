package com.app.gonzalo.pihome.Objetos;

import java.io.Serializable;

/**
 * Created by Gonzalo on 23/02/2015.
 * Define los atributos del objeto para guardar la configuracion en el Fichero
 */
public class StoreDataObject implements Serializable {
    public String nombreDisp, serverPass;

    public StoreDataObject(String nombreDisp, String serverPass) {
        this.nombreDisp = nombreDisp;
        this.serverPass = serverPass;
    }

    @Override
    public String toString() {
        return "StoreDataObject{" +
                "nombreDisp='" + nombreDisp + '\'' +
                ", serverPass='" + serverPass + '\'' +
                '}';
    }

    public String getNombreDisp() {
        return nombreDisp;
    }

    public void setNombreDisp(String nombreDisp) {
        this.nombreDisp = nombreDisp;
    }

    public String getServerPass() {
        return serverPass;
    }

    public void setServerPass(String serverPass) {
        this.serverPass = serverPass;
    }
}
