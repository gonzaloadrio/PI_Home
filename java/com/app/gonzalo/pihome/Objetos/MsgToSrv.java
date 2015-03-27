package com.app.gonzalo.pihome.Objetos;

/**
 * Created by Gonzalo on 23/02/2015.
 * Hereda de la clase Msg y se define para mandar mensajes a la Service SrvPiHome
 */
public class MsgToSrv extends Msg {
    public MsgToSrv(int cmd, Class from, Object object) {
        super(cmd, from, object);
    }
}

