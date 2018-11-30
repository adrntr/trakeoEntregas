package com.example.ingeniera.trakeoentregas.Destino;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.ingeniera.trakeoentregas.Entregas.QrReader;
import com.example.ingeniera.trakeoentregas.Ingreso.SolicitarDestinos;
import com.example.ingeniera.trakeoentregas.R;
import com.example.ingeniera.trakeoentregas.RealTimeLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import static com.example.ingeniera.trakeoentregas.Ingreso.SolicitarDestinos.almacenDestinos;

public class ListaDestinos extends AppCompatActivity {


    private static final int QR_REQUEST = 600;
    RecyclerView recyclerView;
    ListDestinosAdapter listDestinosAdapter;
    Button irATodos;
    private FusedLocationProviderClient mFusedLocationClient;
    RealTimeLocation realTimeLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);
        irATodos=findViewById(R.id.irATodosBt);
        irATodos.setOnClickListener(clicListener);
        recyclerView=findViewById(R.id.recyclerView);


        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listDestinosAdapter = new ListDestinosAdapter(this);
        recyclerView.setAdapter(listDestinosAdapter);


        realTimeLocation=new RealTimeLocation(ListaDestinos.this);
        realTimeLocation.userLocationSettings();


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(ListaDestinos.this);

        setTitle("LISTA - "+ almacenDestinos.getUsuario("nombreApellidoKey"));

        almacenDestinos.setGoogleMapsApp(false);


    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (!almacenDestinos.getGoogleMapsApp("googleMpasAppKey")){
            Intent intent=new Intent(ListaDestinos.this,ListaDestinos.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            finish();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        realTimeLocation.startLocationUpdates(mFusedLocationClient);
    }

    @Override
    protected void onPause() {
        super.onPause();
        realTimeLocation.stopLocationUpdates(mFusedLocationClient);
    }

    View.OnClickListener clicListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.irATodosBt:

                    DireccionesMapsApi direccionesMapsApi=new DireccionesMapsApi(ListaDestinos.this);
                    direccionesMapsApi.irAGoogleMaps();

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
                almacenDestinos.setEstadoRuta(0);
                Intent intent=new Intent(ListaDestinos.this,SolicitarDestinos.class);
                startActivity(intent);
                finish();
                break;
            case R.id.LeerQR:
                Intent intent1=new Intent(ListaDestinos.this,QrReader.class);
                startActivityForResult(intent1,QR_REQUEST);
                break;
            case R.id.MapsApp:
                //almacenDestinos.setEstadoRuta(0);
                Intent intent2=new Intent(ListaDestinos.this,MapsActivity.class);
                startActivity(intent2);
                finish();

                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
