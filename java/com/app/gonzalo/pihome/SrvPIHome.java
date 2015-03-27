package com.app.gonzalo.pihome;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.app.gonzalo.pihome.Objetos.Accion;
import com.app.gonzalo.pihome.Objetos.Dispositivo;
import com.app.gonzalo.pihome.Objetos.MsgToMain;
import com.app.gonzalo.pihome.Objetos.MsgToSrv;
import com.app.gonzalo.pihome.Objetos.OttoMessenger;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

/**
 * Servicio de la Aplicacion, este recibe comandos de otras activities para comunicarse con el Servidor a traves de SocketIO
 */
public class SrvPIHome extends Service {
    public static final String TAG = "Servicio";

    Bus bus;
    Socket socket;

    public SrvPIHome() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //INICIALIZO OTTO
        bus = OttoMessenger.bus;
        bus.register(this);
        initSocketIO();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
// ESTO SE EJECUTA SI EL SERVICIO YA ESTABA EJECUTANDOSE
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Inicializa la comunicacion con SocketIO
     */
    private void initSocketIO() {
        try {
            socket = IO.socket("http://adriohome.ddns.net:8080");

            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    //HACER LO QUE SEA EN LA CONEXION
                    Log.d(TAG, "CONECTADO");
                    Handler mainHandler = new Handler(getApplicationContext().getMainLooper());
                    Runnable myRunnable = new Runnable() {
                        @Override
                        public void run() {
                            bus.post(new MsgToMain(1, this.getClass(), null));
                        }
                    }; // This is your code
                    mainHandler.post(myRunnable);

                }

            }).on("alarma", new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    mostrarNotificacion("ALERTA");
                }

            }).on("respGetDisp", new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    leerJSON(args[0]);
                }

            }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    //HACER LOS QUE SEA AL DESCONECTAR
                    Log.d(TAG, "DESCONECTADO");
                }

            });
            socket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * Lee el JSON recibido y lo guarda en listas
     *
     * @param o
     */
    private void leerJSON(Object o) {

        MainActivity.listaDispositivos.clear();
        MainActivity.listaZonas.clear();

        try {
            JSONObject obj = (JSONObject) o;
            JSONArray dispositivos = obj.getJSONArray("dispositivos");
            for (int i = 0; i < dispositivos.length(); i++) {
                JSONObject disp = (JSONObject) dispositivos.get(i);
                MainActivity.listaDispositivos.add(new Dispositivo(disp.getInt("id"), disp.getString("zona"), disp.getString("tipo"), disp.getString("descripcion")));
            }

            JSONArray zonas = obj.getJSONArray("zonas");
            for (int i = 0; i < zonas.length(); i++) {
                JSONObject zona = (JSONObject) zonas.get(i);
                MainActivity.listaZonas.add(zona.getString("nombre"));
            }
            Log.d(TAG, "LISTAS PREPARADAS");
            Handler mainHandler = new Handler(getApplicationContext().getMainLooper());
            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    bus.post(new MsgToMain(2, this.getClass(), null));
                }
            }; // This is your code
            mainHandler.post(myRunnable);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * Listener de OTTO para obtener los comandos de accion
     *
     * @param data
     */
    @Subscribe
    public void getMessage(MsgToSrv data) {

        if (data.getCmd() == 1) {
            //MUESTRA NOTIFICACION
            mostrarNotificacion((String) data.getObject());
        } else if (data.getCmd() == 2) {
            //ENVIA MENSAJE AL SERVIDOR PARA OBTENER LOS DISPOSITIVOS
            Boolean enviado = false;
            do {
                if (socket.connected()) {
                    socket.emit("getDisp");
                    enviado = true;
                    Log.d(TAG, "Socket.emit -> get / from: " + data.getFrom().getName());
                } else {
                    Log.d(TAG, "Error Socket Desconectado");
                    initSocketIO();
                }
            } while (!enviado);

        } else if (data.getCmd() == 3) {
            //ENVIA MENSAJE AL SERVIDOR PARA QUE ESTE REALICE LA ACCION DEL DISPOSITIVO
            Accion ac = (Accion) data.getObject();
            JSONObject acJSON = new JSONObject();
            try {
                acJSON.put("id", ac.getDispositivo().getId());
                acJSON.put("accion", ac.getAccion());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            socket.emit("setAccion", acJSON);

        } else {
            Log.d(TAG, "Error Comando Incorrecto");
        }
    }

    /**
     * Muestra notificacion
     *
     * @param msg
     */
    private void mostrarNotificacion(String msg) {
        Log.d(TAG, "NOTIFICACION -> " + msg);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(msg)
                .setContentText("Hello World!")
                .setAutoCancel(true);
        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
    }

    /**
     * NO SE USA
     */
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
