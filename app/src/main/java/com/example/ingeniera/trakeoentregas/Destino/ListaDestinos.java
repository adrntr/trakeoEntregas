package com.example.ingeniera.trakeoentregas.Destino;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.ingeniera.trakeoentregas.Entregas.QrReader;
import com.example.ingeniera.trakeoentregas.Ingreso.SolicitarDestinos;
import com.example.ingeniera.trakeoentregas.R;
import com.example.ingeniera.trakeoentregas.RealTimeLocation;
import com.example.ingeniera.trakeoentregas.SingleToast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

import static com.example.ingeniera.trakeoentregas.Ingreso.SolicitarDestinos.almacenDestinos;
import static com.example.ingeniera.trakeoentregas.Ingreso.SolicitarDestinos.estadoRuta;

public class ListaDestinos extends AppCompatActivity{


    private static final int QR_REQUEST = 600;
    RecyclerView recyclerView;
    ListDestinosAdapter listDestinosAdapter;
    Button irATodos;
    FloatingActionButton actualizarDestinosFb;
    private FusedLocationProviderClient mFusedLocationClient;
    RealTimeLocation realTimeLocation;
    SwipeRefreshLayout swipeRefreshLayout;

    CheckBox cumplidoCb,agregadoCb,canceladoCb,pendienteCb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);

        irATodos=findViewById(R.id.irATodosBt);
        irATodos.setOnClickListener(clicListener);
        recyclerView=findViewById(R.id.recyclerView);
        swipeRefreshLayout=findViewById(R.id.swiperefresh);
        cumplidoCb=findViewById(R.id.cumplidosCb);
        agregadoCb=findViewById(R.id.agregadosCb);
        canceladoCb=findViewById(R.id.canceladosCb);
        pendienteCb=findViewById(R.id.pendienteCb);

        canceladoCb.setOnClickListener(clicListener);
        cumplidoCb.setOnClickListener(clicListener);
        pendienteCb.setOnClickListener(clicListener);
        agregadoCb.setOnClickListener(clicListener);

        cumplidoCb.setChecked(almacenDestinos.getFiltros("cumplidoKey"));
        pendienteCb.setChecked(almacenDestinos.getFiltros("pendienteKey"));
        agregadoCb.setChecked(almacenDestinos.getFiltros("agregadoKey"));
        canceladoCb.setChecked(almacenDestinos.getFiltros("canceladoKey"));

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listDestinosAdapter = new ListDestinosAdapter(this);
        recyclerView.setAdapter(listDestinosAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                TaskObtenerDatosRuta taskObtenerDatosRuta = new TaskObtenerDatosRuta(ListaDestinos.this);
                taskObtenerDatosRuta.execute(almacenDestinos.getIdHojaDeRuta());
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        realTimeLocation=new RealTimeLocation(ListaDestinos.this);
        realTimeLocation.userLocationSettings();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(ListaDestinos.this);

        ArrayList<Usuarios> usuarios=almacenDestinos.getArrayUsuarios("arrayUsuariosKey");
        for (int i=0;i<usuarios.size();i++){
            if (usuarios.get(i).getTipo().equals("Responsable")){
                setTitle("Hoja Nº"+almacenDestinos.getIdHojaDeRuta()+" - "+usuarios.get(i).getNombre());
            }
        }


        almacenDestinos.setGoogleMapsApp(false);


    }

    @Override
    protected void onRestart() {
        super.onRestart();
        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        realTimeLocation.startLocationUpdates(mFusedLocationClient);

        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.getAdapter().notifyDataSetChanged();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    @Override
    protected void onPause() {
        super.onPause();
        realTimeLocation.stopLocationUpdates(mFusedLocationClient);
    }



    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((mMessageReceiver), new IntentFilter("MyData")); //comienza a escuchar los broadcast con el filtro "MiData"
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);//deja de escuchar los broadcast
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() { //se ejecuta cuando en TaskOBtenerDatosRuta se llama a la funcion sendBroadcast
        @Override
        public void onReceive(Context context, Intent intent) {
            recyclerView.getAdapter().notifyDataSetChanged();
            SingleToast.show(ListaDestinos.this,"UN DESTINO FUE AGREGADO",Toast.LENGTH_SHORT);
        }
    };






    View.OnClickListener clicListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.irATodosBt:

                    DireccionesMapsApi direccionesMapsApi=new DireccionesMapsApi(ListaDestinos.this);
                    direccionesMapsApi.irAGoogleMaps();

                    break;

                    default:
                        almacenDestinos.setFiltros(pendienteCb.isChecked(),cumplidoCb.isChecked(),canceladoCb.isChecked(),agregadoCb.isChecked());
                        recyclerView.getAdapter().notifyDataSetChanged();
                        break;

            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.listaactivity_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();

        switch (id){

            case R.id.Terminar:

                TaskCancerlarHojaDeRuta taskCancerlarHojaDeRuta = new TaskCancerlarHojaDeRuta(ListaDestinos.this);
                taskCancerlarHojaDeRuta.execute(almacenDestinos.getIdHojaDeRuta());

                break;
            case R.id.LeerQR:
                Intent intent1=new Intent(ListaDestinos.this,QrReader.class);
                startActivityForResult(intent1,QR_REQUEST);
                break;
            case R.id.MapsApp:
                Intent intent2=new Intent(ListaDestinos.this,MapsActivity.class);
                intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent2);
                //finish();

                break;
        }

        return super.onOptionsItemSelected(item);
    }


}
