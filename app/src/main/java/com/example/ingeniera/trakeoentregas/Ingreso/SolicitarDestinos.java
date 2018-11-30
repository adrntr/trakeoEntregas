package com.example.ingeniera.trakeoentregas.Ingreso;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.JsonObjectRequest;
import com.example.ingeniera.trakeoentregas.AlmacenDestinos;
import com.example.ingeniera.trakeoentregas.Destino.Destinos;
import com.example.ingeniera.trakeoentregas.Destino.ListaDestinos;
import com.example.ingeniera.trakeoentregas.R;

import java.util.ArrayList;

public class SolicitarDestinos extends AppCompatActivity {

    private static final int LOCATION_REQUEST = 500;
    public EditText codigoEt;
    public Button consultarBt,cambiarDniBt;
    public TextView nombreIngresoTv;
    private String url = "https://sistemas.andif.com.ar/pruebas/prueba-remito-transporte/datos-planilla-seguro.php";
    private String codigo = "id_planilla";

    public ArrayList<Destinos> destinos;
    JsonObjectRequest jsonObjectRequest;
    ProgressDialog progress;
    Context context;
    public static AlmacenDestinos almacenDestinos; //defino
    public static int estadoRuta;
    Boolean hojasDeRutaReady=false;

    RecyclerView RecyclerviewSolDes;
    HojasDeRutaAdapter hojasDeRutaAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitar_destinos);

        almacenDestinos = new AlmacenDestinos(this);
        if (almacenDestinos.getEstadoRuta() > 1) {
            Intent intent = new Intent(SolicitarDestinos.this, ListaDestinos.class);
            startActivity(intent);
            finish();
        }

        codigoEt = findViewById(R.id.codigoEt);
        consultarBt = findViewById(R.id.consultarBt);
        cambiarDniBt=findViewById(R.id.cambiarDniBt);
        nombreIngresoTv=findViewById(R.id.nombreIngresoTv);

        consultarBt.setOnClickListener(ClickListener);
        cambiarDniBt.setOnClickListener(ClickListener);


        destinos = new ArrayList<>();


        estadoRuta = almacenDestinos.getEstadoRuta();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            return;
        }

        if(almacenDestinos.getEstadoRuta()==1){
            codigoEt.setVisibility(View.GONE);
            consultarBt.setVisibility(View.GONE);
            nombreIngresoTv.setVisibility(View.VISIBLE);
            cambiarDniBt.setVisibility(View.VISIBLE);

            nombreIngresoTv.setText(almacenDestinos.getUsuario("nombreApellidoKey"));
            RecyclerviewSolDes=findViewById(R.id.RecyclerviewSolDes);
            RecyclerviewSolDes.setHasFixedSize(true);

            RecyclerviewSolDes.setLayoutManager(new LinearLayoutManager(this));

            hojasDeRutaAdapter = new HojasDeRutaAdapter(this);
            RecyclerviewSolDes.setAdapter(hojasDeRutaAdapter);
            setTitle("RUTAS - "+almacenDestinos.getUsuario("nombreApellidoKey"));
        }else if (almacenDestinos.getEstadoRuta()==0){
            codigoEt.setVisibility(View.VISIBLE);
            consultarBt.setVisibility(View.VISIBLE);
            nombreIngresoTv.setVisibility(View.GONE);
            cambiarDniBt.setVisibility(View.GONE);

            setTitle("INGRESO");

        }

        Toast.makeText(SolicitarDestinos.this,String.valueOf("Estado= "+almacenDestinos.getEstadoRuta()),Toast.LENGTH_SHORT).show();
    }

    View.OnClickListener ClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()){

                case R.id.consultarBt:
                    String dni = codigoEt.getText().toString();
                    if (dni != null) {

                        TaskValidarDNI validarDNI = new TaskValidarDNI(SolicitarDestinos.this);
                        validarDNI.execute("dni",dni);
                        almacenDestinos.setEstadoRuta(0);

                    }
                    break;
                case R.id.cambiarDniBt:
                    almacenDestinos.setEstadoRuta(0);
                    almacenDestinos.setUsuario("","");
                    Intent intent=new Intent(SolicitarDestinos.this,SolicitarDestinos.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    finish();


            }


        }
    };


    @Override
    protected void onRestart() {
        super.onRestart();
        if (almacenDestinos.getEstadoRuta() >1) {
            finish();
        }
    }

}
