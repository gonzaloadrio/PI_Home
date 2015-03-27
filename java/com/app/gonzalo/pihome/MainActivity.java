package com.app.gonzalo.pihome;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.app.gonzalo.pihome.Objetos.Dispositivo;
import com.app.gonzalo.pihome.Objetos.MsgToMain;
import com.app.gonzalo.pihome.Objetos.MsgToSrv;
import com.app.gonzalo.pihome.Objetos.OttoMessenger;
import com.app.gonzalo.pihome.Objetos.StoreDataObject;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    public static final String TAG = "MAIN";
    public static final String configFile = "config.dat";


    public static ArrayList<Dispositivo> listaDispositivos = new ArrayList<>();
    public static ArrayList<String> listaZonas = new ArrayList<>();

    public static StoreDataObject sdo;
    public boolean isConected;
    private boolean isPrepared;
    AlertDialog dialog;

    Bus bus;


    @Override
    protected void onPause() {
        super.onPause();
        //IMPORTANTE
        bus.unregister(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //IMPORTANTE
        bus = OttoMessenger.bus;
        bus.register(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // INICIO EL SERVICIO
        startService(new Intent(MainActivity.this, SrvPIHome.class));
        leerConfiguracion();
        if (!hayConfig()) {
            mostrarConfiguracion();
        } else {
            try{
                bus.post(new MsgToSrv(2, this.getClass(),null));
            }catch (Exception ex){

            }
            mostrarPantalla();
        }

    }

    /**
     * Carga el Fragment
     */
    private void mostrarPantalla() {
        Log.d(TAG, "mostrarPantalla");

        setContentView(R.layout.activity_main);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentA, new ListaOpciones());
        fragmentTransaction.commit();


        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage("\nAplicion de Domotica\n\n" +
                "Creada por:\n\n" +
                "Gonzalo Adrio Bernardez\n\n")
                .setTitle("Acerca De");
        // 3. Get the AlertDialog from create()
        dialog = builder.create();
    }

    /**
     * Lanza la Activity de Configuracion
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void mostrarConfiguracion() {
        Intent i = new Intent(MainActivity.this, Configuracion.class);
        i.putExtra("CONFIG", hayConfig());
        startActivityForResult(i, 1, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            mostrarPantalla();
        }
    }

    /**
     * Lee el fichero de configuracion
     */
    private void leerConfiguracion() {
        ObjectInputStream ois = null;

        try {
            ois = new ObjectInputStream(openFileInput(configFile));
            sdo = (StoreDataObject) ois.readObject();
            ois.close();
        } catch (IOException e) {
            Log.d(TAG, e.toString());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * Devuelve si hay configuracion
     */
    private boolean hayConfig() {
        return sdo != null ? true : false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //Muestra los botones del ActionBar
        if (id == R.id.action_settings) {
            mostrarConfiguracion();
            return true;
        } else if (id == R.id.about) {
            dialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Listener de OTTO para recibir los mensajes
     * @param data
     */
    @Subscribe
    public void getMessage(MsgToMain data) {

        if (data.getCmd() == 1) {
            isConected = true;
            bus.post(new MsgToSrv(2, this.getClass(), null));
        } else if (data.getCmd() == 2) {
            isPrepared = true;
            mostrarPantalla();
        }
    }
}
