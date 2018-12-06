package com.example.ingeniera.trakeoentregas.Ingreso;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.JsonObjectRequest;
import com.example.ingeniera.trakeoentregas.AlmacenDestinos;
import com.example.ingeniera.trakeoentregas.Destino.Destinos;
import com.example.ingeniera.trakeoentregas.Destino.ListaDestinos;
import com.example.ingeniera.trakeoentregas.Destino.Usuarios;
import com.example.ingeniera.trakeoentregas.R;

import java.util.ArrayList;

import static android.text.InputType.TYPE_CLASS_NUMBER;

public class SolicitarDestinos extends AppCompatActivity {

    private static final int LOCATION_REQUEST = 500;
    public EditText codigoEt;
    FloatingActionButton agregarAcompañanteBt,consultarBt,cambiarDniBt;
    private TextView responsableTv,seleccionarHojaTv,registroLoginTv;
    private View divider3;

    public ArrayList<Destinos> destinos;
    JsonObjectRequest jsonObjectRequest;
    ProgressDialog progress;
    Context context;
    public static AlmacenDestinos almacenDestinos; //defino
    public static int estadoRuta;
    Boolean hojasDeRutaReady=false;


    RecyclerView RecyclerviewSolDes;
    private RecyclerView recyclerViewUsuarios;
    HojasDeRutaAdapter hojasDeRutaAdapter;
    UsuariosAdapter usuariosAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitar_destinos);
        almacenDestinos = new AlmacenDestinos(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            almacenDestinos.setEstadoRuta(0);
        }


        if (almacenDestinos.getEstadoRuta() > 1) {
            Intent intent = new Intent(SolicitarDestinos.this, ListaDestinos.class);
            startActivity(intent);
            finish();
        }



        codigoEt = findViewById(R.id.codigoEt);
        consultarBt = findViewById(R.id.consultarBt);
        cambiarDniBt=findViewById(R.id.cambiarDniBt);
        agregarAcompañanteBt=findViewById(R.id.agregarAcompañanteBt);
        responsableTv=findViewById(R.id.responsablesTv);
        seleccionarHojaTv=findViewById(R.id.seleccionarHojaTv);
        divider3=findViewById(R.id.divider3);
        registroLoginTv=findViewById(R.id.registroLoginTv);

        consultarBt.setOnClickListener(ClickListener);
        cambiarDniBt.setOnClickListener(ClickListener);
        agregarAcompañanteBt.setOnClickListener(ClickListener);

        destinos = new ArrayList<>();


        estadoRuta = almacenDestinos.getEstadoRuta();


        if(almacenDestinos.getEstadoRuta()==1){
            codigoEt.setVisibility(View.GONE);
            consultarBt.setVisibility(View.GONE);
            registroLoginTv.setVisibility(View.GONE);
            cambiarDniBt.setVisibility(View.VISIBLE);
            agregarAcompañanteBt.setVisibility(View.VISIBLE);
            seleccionarHojaTv.setVisibility(View.VISIBLE);
            responsableTv.setVisibility(View.VISIBLE);
            divider3.setVisibility(View.VISIBLE);
            //recyclerViewUsuarios.setVisibility(View.VISIBLE);
            //RecyclerviewSolDes.setVisibility(View.VISIBLE);

            RecyclerviewSolDes=findViewById(R.id.RecyclerviewSolDes);
            RecyclerviewSolDes.setHasFixedSize(true);
            RecyclerviewSolDes.setLayoutManager(new LinearLayoutManager(this));
            hojasDeRutaAdapter = new HojasDeRutaAdapter(this);
            RecyclerviewSolDes.setAdapter(hojasDeRutaAdapter);

            recyclerViewUsuarios=findViewById(R.id.recyclerViewUsuarios);
            recyclerViewUsuarios.setHasFixedSize(true);
            recyclerViewUsuarios.setLayoutManager(new LinearLayoutManager(this));
            usuariosAdapter = new UsuariosAdapter(this);
            recyclerViewUsuarios.setAdapter(usuariosAdapter);




            ArrayList<Usuarios> usuarios=almacenDestinos.getArrayUsuarios("arrayUsuariosKey");
            for (int i=0;i<usuarios.size();i++){
                if (usuarios.get(i).getTipo().equals("Responsable")){
                    setTitle("RUTAS - "+usuarios.get(i).getNombre());
                    //nombreIngresoTv.setText(usuarios.get(i).getNombre());
                }else {
                    //nombreIngresoTv.setText(nombreIngresoTv.getText()+"\n\t"+usuarios.get(i).getNombre());
                }
            }

        }else if (almacenDestinos.getEstadoRuta()==0){
            codigoEt.setVisibility(View.VISIBLE);
            consultarBt.setVisibility(View.VISIBLE);
            registroLoginTv.setVisibility(View.VISIBLE);
            seleccionarHojaTv.setVisibility(View.GONE);
            responsableTv.setVisibility(View.GONE);
            cambiarDniBt.setVisibility(View.GONE);
            agregarAcompañanteBt.setVisibility(View.GONE);
            divider3.setVisibility(View.GONE);
            //recyclerViewUsuarios.setVisibility(View.GONE);
            //RecyclerviewSolDes.setVisibility(View.GONE);

            codigoEt.setText(null);

            setTitle("INGRESO");

        }


    }

    View.OnClickListener ClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()){

                case R.id.consultarBt:
                    String dni = codigoEt.getText().toString();
                    if (dni != null) {
                        if (Integer.parseInt(dni)>1000000){
                            TaskValidarDNI validarDNI = new TaskValidarDNI(SolicitarDestinos.this);
                            validarDNI.execute(dni);
                            almacenDestinos.setEstadoRuta(0);
                            codigoEt.setText("");
                        }else{
                            Toast.makeText(SolicitarDestinos.this,"DNI invalido",Toast.LENGTH_SHORT).show();
                        }

                    }
                    break;
                case R.id.cambiarDniBt:
                    almacenDestinos.setEstadoRuta(0);
                    almacenDestinos.setArrayUsuarios(new ArrayList<Usuarios>());
                    Intent intent=new Intent(SolicitarDestinos.this,SolicitarDestinos.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.agregarAcompañanteBt:
                    final EditText input = new EditText(SolicitarDestinos.this);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);
                    input.setLayoutParams(lp);
                    input.setInputType(TYPE_CLASS_NUMBER);
                    new AlertDialog.Builder(SolicitarDestinos.this)
                            .setTitle("AGREGAR ACOMPAÑANTE")
                            .setMessage("Ingrese su DNI")
                            .setView(input)
                            .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String dniAcompañante= String.valueOf(input.getText());
                                    ArrayList<Usuarios> acompañantes =almacenDestinos.getArrayUsuarios("arrayUsuariosKey");
                                    Boolean existe=false;
                                    for (int i=0;i<acompañantes.size();i++){
                                        if (dniAcompañante.equals(acompañantes.get(i).getDni())){
                                            Toast.makeText(SolicitarDestinos.this,"El dni "+dniAcompañante+" ya se encuentra en uso",Toast.LENGTH_SHORT).show();
                                            existe=true;
                                        }
                                    }
                                    if (!existe){
                                        TaskValidarAcompanante taskValidarAcompanante = new TaskValidarAcompanante(SolicitarDestinos.this);
                                        taskValidarAcompanante.execute(dniAcompañante);
                                    }

                                }
                            })
                            .setNegativeButton("Cancelar",null)
                            .show();
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
