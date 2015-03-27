package com.app.gonzalo.pihome.Objetos;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Created by Gonzalo on 23/02/2015.
 * Define el objeto estatico de Bus
 */
public class OttoMessenger {
    public static Bus bus = new Bus(ThreadEnforcer.MAIN);
}
