package com.app.gonzalo.pihome;


import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.gonzalo.pihome.Objetos.Dispositivo;
import com.app.gonzalo.pihome.Objetos.MsgToOps;
import com.app.gonzalo.pihome.Objetos.MsgToSrv;
import com.app.gonzalo.pihome.Objetos.OttoMessenger;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;


/**
 * Este Fragment muestra la lista de Zonas Disponibles *
 */
public class ListaOpciones extends Fragment {

    public static final String TAG = "FRAGMENT LISTA OPCIONES";
    Bus bus;

    ListView listView;
    ArrayList<Dispositivo> dispositivos;
    ArrayList<String> zonas;
    ProgressDialog progress;
    Boolean hayDatos = false;

    public ListaOpciones() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_lista_opciones, container, false);
    }

    @Override
    public void onPause() {
        super.onPause();
        bus.unregister(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        bus = OttoMessenger.bus;
        bus.register(this);

        //Inicializon los controles
        dispositivos = MainActivity.listaDispositivos;
        zonas = MainActivity.listaZonas;
        progress = new ProgressDialog(getActivity());

        // get the listview
        listView = (ListView) getActivity().findViewById(R.id.listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, zonas.get(position));

                //Lanzo la activity que muestra los dispositivos
                Intent i = new Intent(getActivity(), ControlDispActivity.class);
                i.putExtra("posicion", position);
                startActivity(i);
            }
        });

        actualizarLista();

    }

    /**
     * Manda un mensaje al Servicio para que le responda con los dispositivos
     */
    public void cargarDispositivos() {
        bus.post(new MsgToSrv(2, this.getClass(), null));
    }


    @Subscribe
    public void getMessage(MsgToOps data) {
    }

    /**
     * Actualiza la lista de zonas disponibles
     */
    private void actualizarLista() {
        ArrayAdapter adapter = new ArrayAdapter(getActivity().getApplicationContext(), R.layout.list_view_zonas, R.id.text1, zonas) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                int numDisp = 0;
                for (int i = 0; i < dispositivos.size(); i++) {
                    if (dispositivos.get(i).getZona().equals(zonas.get(position))) {
                        numDisp++;
                    }
                }
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(R.id.text1);
                text1.setText(Character.toUpperCase(zonas.get(position).charAt(0)) + zonas.get(position).substring(1));
                TextView text3 = (TextView) view.findViewById(R.id.text3);
                text3.setText("Tiene: " + numDisp + " dispositivos");
                ImageView icono = (ImageView) view.findViewById(R.id.sw);
                Resources resources = getActivity().getApplicationContext().getResources();
                final int resourceId = resources.getIdentifier(zonas.get(position), "drawable", getActivity().getApplicationContext().getPackageName());
                icono.setImageResource(resourceId);
                return view;
            }
        };
        listView.setAdapter(adapter);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            cargarDispositivos();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
