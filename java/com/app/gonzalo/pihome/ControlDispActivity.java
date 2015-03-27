package com.app.gonzalo.pihome;


import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.app.gonzalo.pihome.Objetos.Accion;
import com.app.gonzalo.pihome.Objetos.Dispositivo;
import com.app.gonzalo.pihome.Objetos.MsgToSrv;
import com.app.gonzalo.pihome.Objetos.OttoMessenger;
import com.squareup.otto.Bus;

import java.util.ArrayList;


/**
 * Activity que muestra la lista de los dispositivos de la zona seleccionada
 */
public class ControlDispActivity extends ActionBarActivity {

    public static final String TAG = "CONTROL";
    Bus bus;
    ListView listView;
    int pos;
    ArrayList<Dispositivo> lista = new ArrayList<>();
    Button btn;
    Switch sw;

    @Override
    public void onPause() {
        super.onPause();
        // Importante desregistrar el bus
        bus.unregister(this);
    }


    @Override
    public void onResume() {
        super.onResume();
        // Importante registrar el bus
        bus = OttoMessenger.bus;
        bus.register(this);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_panel_control);

        pos = getIntent().getIntExtra("posicion", -1);
        listView = (ListView) findViewById(R.id.listView2);
        actualizarListaDisp();
    }

    /**
     * Actualiza la lista de dispositivos
     */
    private void actualizarListaDisp() {


        if (pos > -1) {
            for (int i = 0; i < MainActivity.listaDispositivos.size(); i++) {
                if (MainActivity.listaDispositivos.get(i).getZona().equals(MainActivity.listaZonas.get(pos))) {
                    lista.add(MainActivity.listaDispositivos.get(i));
                }
            }


            ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), R.layout.list_view_control, R.id.text12, lista) {
                @Override

                public View getView(final int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);

                    final Dispositivo disp = lista.get(position);
                    TextView text1 = (TextView) view.findViewById(R.id.text12);
                    text1.setText(Character.toUpperCase(disp.getTipo().charAt(0)) + disp.getTipo().substring(1));

                    TextView text3 = (TextView) view.findViewById(R.id.text32);
                    text3.setText(Character.toUpperCase(disp.getDescripcion().charAt(0)) + disp.getDescripcion().substring(1));

                    LinearLayout layout = (LinearLayout) view.findViewById(R.id.obj);

                    btn = new Button(view.getContext());
                    sw = new Switch(view.getContext());
                    btn.setText("ACCION");

                    //IMPORTANTE esto redibuja la lista de dispositivos cuando es demasiado grande y no entra en toda la pantalla
                    layout.removeAllViews();

                    if (disp.getTipo().equals("luz")) {
                        layout.addView(sw);
                    } else {
                        layout.addView(btn);
                    }
                    // Inicializo los listener de los controles
                    sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            Log.d(TAG, disp.getId() + " - " + isChecked);
                            boolean estado = isChecked;
                            bus.post(new MsgToSrv(3, this.getClass(), new Accion(estado, disp)));
                        }
                    });

                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d(TAG, disp.getId() + " - true");
                            bus.post(new MsgToSrv(3, this.getClass(), new Accion(true, disp)));
                        }
                    });

                    return view;
                }
            };

            listView.setAdapter(adapter);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
