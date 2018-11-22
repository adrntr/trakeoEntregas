package com.example.ingeniera.trakeoentregas;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SolicitarDestinos extends AppCompatActivity {

    public EditText codigoEt;
    public static TextView jsonTv;
    public Button consultarBt;
    private String url = "https://sistemas.andif.com.ar/pruebas/prueba-remito-transporte/datos-planilla-seguro.php";
    private String codigo = "id_planilla";

    public ArrayList<Destinos> destinos;
    JsonObjectRequest jsonObjectRequest;
    ProgressDialog progress;
    Context context;
    public static AlmacenDestinos almacenDestinos; //defino
    public static int estadoRuta;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitar_destinos);

        almacenDestinos = new AlmacenDestinos(this);
        if (almacenDestinos.getEstadoRuta() != 0) {
            Intent intent = new Intent(SolicitarDestinos.this, MapsActivity.class);
            startActivity(intent);
            finish();
        }
        codigoEt = findViewById(R.id.codigoEt);
        jsonTv = findViewById(R.id.jsonTv);
        consultarBt = findViewById(R.id.consultarBt);

        consultarBt.setOnClickListener(ClickListener);

        destinos = new ArrayList<>();


        estadoRuta = almacenDestinos.getEstadoRuta();
    }

    View.OnClickListener ClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String numeroPedido = codigoEt.getText().toString();
            if (numeroPedido != null) {
                TaskObtenerDatosRuta obtenerDatosRuta = new TaskObtenerDatosRuta(SolicitarDestinos.this);
                obtenerDatosRuta.execute(codigo, numeroPedido);

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
