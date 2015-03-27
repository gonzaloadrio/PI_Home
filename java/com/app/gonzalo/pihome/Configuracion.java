package com.app.gonzalo.pihome;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.app.gonzalo.pihome.Objetos.MsgToSrv;
import com.app.gonzalo.pihome.Objetos.OttoMessenger;
import com.app.gonzalo.pihome.Objetos.StoreDataObject;
import com.squareup.otto.Bus;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Activity que muestra un panel de configuracion
 */
public class Configuracion extends ActionBarActivity {

    public static final String TAG = "Configuracion";

    Button bReset, bTest;
    EditText etNom, etPass;
    CheckBox cBox;
    String nom, pass;

    Bus bus;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        //Inicializa el bus de OTTO
        bus = OttoMessenger.bus;
        bus.register(this);

        //Recoge el intent
        Intent i = getIntent();

        //Inicializa los controles
        etPass = (EditText) findViewById(R.id.etPass);
        etNom = (EditText) findViewById(R.id.etNomDisp);
        cBox = (CheckBox) findViewById(R.id.checkbox);

        bReset = (Button) findViewById(R.id.bReset);
        bTest = (Button) findViewById(R.id.bTestCon);

        // Comprueba los datos recibidos en el intent
        if (i.getBooleanExtra("CONFIG", false)) {
            ObjectInputStream ois = null;

            try {
                ois = new ObjectInputStream(openFileInput(MainActivity.configFile));
                StoreDataObject sdo = (StoreDataObject) ois.readObject();
                etNom.setText(sdo.getNombreDisp());
                etPass.setText(sdo.getServerPass());
                ois.close();
            } catch (IOException e) {
                Log.d(TAG, e.toString());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        // Inicializo los listener de los controles
        cBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //Comprueba si está checkeado para ocultar o mostrar la contraseña
                if (cBox.isChecked()) {
                    etPass.setInputType(144);
                } else {
                    etPass.setInputType(129);
                }
                Log.d(TAG, etPass.getInputType() + "");
                etPass.setSelection(etPass.length());

            }
        });
        bReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Limpio todos los controles
                nom = null;
                pass = null;
                etNom.setText("");
                etPass.setText("");
                etNom.requestFocus();
            }
        });


        bTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Envio mensaje al Servicio para que muestre una notificacion
                bus.post(new MsgToSrv(1, this.getClass(), "HOLA"));
            }
        });

    }

    /**
     * Guarda la configuracion
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void guardar() {
        //Obtiene los datos de los controles y comprueba si estan correctamente
        nom = String.valueOf(etNom.getText());
        pass = String.valueOf(etPass.getText());
        if (!nom.equals("") && !pass.equals("")) {
            escribirConfiguracion(nom, pass);
            finishAfterTransition();
        } else {
            Toast.makeText(getApplication(), "Rellena bien los campos de texto", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Escribe los datos en un fichero
     *
     * @param nom
     * @param pass
     */
    private void escribirConfiguracion(String nom, String pass) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;

        try {
            fos = openFileOutput(MainActivity.configFile, Context.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            //Inicializo el objeto StoreDataObject para guardarlo en el fichero
            StoreDataObject sdo = new StoreDataObject(nom, pass);
            oos.writeObject(sdo);
            MainActivity.sdo = sdo;
            Log.d(TAG, "Escrito en fichero " + sdo.toString());
            oos.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, e.toString());
        } catch (IOException e) {
            Log.d(TAG, e.toString());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bus.unregister(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_config, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //Agrago el boton de guardar
        if (id == R.id.action_save) {
            guardar();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
