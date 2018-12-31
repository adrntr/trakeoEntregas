package com.example.ingeniera.trakeoentregas;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.location.Location;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ingeniera.trakeoentregas.Destino.ListaDestinos;
import com.example.ingeniera.trakeoentregas.Destino.MapsActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import org.w3c.dom.Text;

import java.security.Signature;

import static com.example.ingeniera.trakeoentregas.Ingreso.SolicitarDestinos.almacenDestinos;

public class CambiarDireccion extends AppCompatActivity {

    private EditText calleCambiarEt,numeroCambiarEt,localidadCambiarEt,provinciaCambiarEt;
    private FloatingActionButton convertiDirBt;
    private Button usarPosActualBt;

    ConvertirLatLng convertirLatLng;

    private int idDestino;

    private FusedLocationProviderClient mFusedLocationClient;
    RealTimeLocation realTimeLocation;
    private Location mCurrentLocation;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_direccion);

        calleCambiarEt=findViewById(R.id.calleCambiarEt);
        numeroCambiarEt=findViewById(R.id.numeroCambiarEt);
        localidadCambiarEt=findViewById(R.id.localidadCambiarEt);
        provinciaCambiarEt=findViewById(R.id.provinciaCambiarEt);
        convertiDirBt=findViewById(R.id.convertirDirBt);
        usarPosActualBt=findViewById(R.id.usarPosActualBt);

        convertiDirBt.setOnClickListener(ClicListener);
        usarPosActualBt.setOnClickListener(ClicListener);

        Bundle extras = getIntent().getExtras();
        idDestino=extras.getInt("id");

        realTimeLocation=new RealTimeLocation(CambiarDireccion.this);
        realTimeLocation.userLocationSettings();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(CambiarDireccion.this);



        convertirLatLng=new ConvertirLatLng(CambiarDireccion.this);

        //obtain last user's location
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    mCurrentLocation = new Location(location);
                    convertirLatLng.startIntentService(mCurrentLocation); //convierto la ubicacion en una dirección
                }
            }
        });
    }

    View.OnClickListener ClicListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.convertirDirBt:
                    String calle=calleCambiarEt.getText().toString();
                    String numero=numeroCambiarEt.getText().toString();
                    String localidad=localidadCambiarEt.getText().toString();
                    String provincia=provinciaCambiarEt.getText().toString();

                    if (calle.equals("")||numero.equals("")){
                        SingleToast.show(CambiarDireccion.this,"Coloque calle y número",Toast.LENGTH_SHORT);
                    }else{
                        SingleToast.show(CambiarDireccion.this,calle+" "+numero + ", "+localidad+", "+provincia,Toast.LENGTH_SHORT );
                        TaskCambiarDireccion taskCambiarDireccion=new TaskCambiarDireccion(CambiarDireccion.this,idDestino);
                        taskCambiarDireccion.execute(calle+" "+numero + ", "+localidad+" "+provincia);
                    }
                    break;
                case R.id.usarPosActualBt:
                    final String currentAddress= almacenDestinos.getCurrentAddress();
                    SingleToast.show(CambiarDireccion.this,currentAddress,0);
                    final String lat= almacenDestinos.getLat();
                    final String lng=almacenDestinos.getLng();
                    if (currentAddress.matches(".*\\d+.*")){ //si tiene un numero

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CambiarDireccion.this);
                        alertDialogBuilder
                                .setTitle("Cambiar por " + currentAddress)
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        TaskEnviarDireccion taskEnviarDireccion = new TaskEnviarDireccion(CambiarDireccion.this,idDestino,Double.parseDouble(lat),Double.parseDouble(lng),currentAddress);
                                        taskEnviarDireccion.execute();
                                    }
                                })
                                .setNegativeButton("Cancel",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }else {
                        SingleToast.show(CambiarDireccion.this,"No se encuentra su ubicación actual",0);
                    }

            }
        }
    };


    @SuppressLint("MissingPermission")
    @Override
    protected void onResume() {
        super.onResume();
        realTimeLocation.startLocationUpdates(mFusedLocationClient);
        //obtain last user's location
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    mCurrentLocation = new Location(location);
                    convertirLatLng.startIntentService(mCurrentLocation); //convierto la ubicacion en una dirección
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        realTimeLocation.stopLocationUpdates(mFusedLocationClient);
    }


}
