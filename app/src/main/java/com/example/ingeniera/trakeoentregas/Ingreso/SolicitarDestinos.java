package com.example.ingeniera.trakeoentregas.Ingreso;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.JsonObjectRequest;
import com.example.ingeniera.trakeoentregas.AlmacenDestinos;
import com.example.ingeniera.trakeoentregas.Destinos;
import com.example.ingeniera.trakeoentregas.MapsActivity;
import com.example.ingeniera.trakeoentregas.R;
import com.example.ingeniera.trakeoentregas.TaskObtenerDatosRuta;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class SolicitarDestinos extends AppCompatActivity {

    private static final int LOCATION_REQUEST = 500;
    public EditText codigoEt;
    public Button consultarBt;
    private String url = "https://sistemas.andif.com.ar/pruebas/prueba-remito-transporte/datos-planilla-seguro.php";
    private String codigo = "id_planilla";

    public ArrayList<Destinos> destinos;
    JsonObjectRequest jsonObjectRequest;
    ProgressDialog progress;
    Context context;
    public static AlmacenDestinos almacenDestinos; //defino
    public static int estadoRuta;
    Boolean hojasDeRutaReady=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitar_destinos);

        almacenDestinos = new AlmacenDestinos(this);
        if (almacenDestinos.getEstadoRuta() > 1) {
            Intent intent = new Intent(SolicitarDestinos.this, MapsActivity.class);
            startActivity(intent);
            finish();
        }

        codigoEt = findViewById(R.id.codigoEt);
        consultarBt = findViewById(R.id.consultarBt);

        consultarBt.setOnClickListener(ClickListener);

        destinos = new ArrayList<>();


        estadoRuta = almacenDestinos.getEstadoRuta();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            return;
        }

        if(almacenDestinos.getEstadoRuta()==1){
            codigoEt.setText(almacenDestinos.getNombreApellido());
        }

        Toast.makeText(SolicitarDestinos.this,String.valueOf("Estado= "+almacenDestinos.getEstadoRuta()),Toast.LENGTH_SHORT).show();
    }

    View.OnClickListener ClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String dni = codigoEt.getText().toString();
            if (dni != null) {

                TaskValidarDNI validarDNI = new TaskValidarDNI(SolicitarDestinos.this);
                validarDNI.execute("dni",dni);

            }

        }
    };


    @Override
    protected void onRestart() {
        super.onRestart();
        if (almacenDestinos.getEstadoRuta() != 0) {
            finish();
        }
    }

}
