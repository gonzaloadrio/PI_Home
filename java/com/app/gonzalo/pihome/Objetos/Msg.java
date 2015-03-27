package com.app.gonzalo.pihome.Objetos;

/**
 * Created by Gonzalo on 27/02/2015.
 * Define los atributos para el intercambio de informacion con OTTO
 */
public class Msg {
    int cmd;
    Class from;
    Object object;

    public Msg(int cmd, Class from, Object object) {
        this.cmd = cmd;
        this.from = from;
        this.object = object;
    }

    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    public Class getFrom() {
        return from;
    }

    public void setFrom(Class from) {
        this.from = from;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
