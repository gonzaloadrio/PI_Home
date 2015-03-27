package com.app.gonzalo.pihome.Objetos;

/**
 * Created by Gonzalo on 26/02/2015.
 * Hereda de la clase Msg y se define para mandar mensajes a la Activity ControlDispActivity
 */
public class MsgToCtrl extends Msg{
    public MsgToCtrl(int cmd, Class from, Object object) {
        super(cmd, from, object);
    }
}
