package com.example.ingeniera.trakeoentregas;

import android.app.ProgressDialog;
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
    private String url= "https://sistemas.andif.com.ar/pruebas/prueba-remito-transporte/datos-planilla-seguro.php";
    private String codigo="id_planilla";

    public ArrayList<Destinos> destinos;
    JsonObjectRequest jsonObjectRequest;
    ProgressDialog progress;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitar_destinos);

        codigoEt=findViewById(R.id.codigoEt);
        jsonTv=findViewById(R.id.jsonTv);
        consultarBt=findViewById(R.id.consultarBt);

        consultarBt.setOnClickListener(ClickListener);

        destinos=new ArrayList<>();

    }

    View.OnClickListener ClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String numeroPedido=codigoEt.getText().toString();
            obtenerDatos(numeroPedido);
        }
    };


    private void obtenerDatos(final String numPedidos) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://sistemas.andif.com.ar/pruebas/prueba-remito-transporte/datos-planilla-seguro.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(SolicitarDestinos.this, response, Toast.LENGTH_LONG).show();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("registros");
                    Destinos destino;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        destino = new Destinos();
                        JSONObject jsonObjectExplorer = jsonArray.getJSONObject(i);
                        destino.setIdCliente(jsonObjectExplorer.optInt("id_cliente"));
                        destino.setCantidadBultos(jsonObjectExplorer.optInt("cantidad_bultos"));
                        destino.setNombre_cliente(jsonObjectExplorer.optString("nombre_cliente"));
                        destino.setTransporte(jsonObjectExplorer.optString("transporte"));
                        destino.setDireccion_transporte(jsonObjectExplorer.optString("direccion_transporte"));
                        destino.setLatitude(jsonObjectExplorer.optDouble("latitud"));
                        destino.setLongitude(jsonObjectExplorer.optDouble("longitud"));
                        destinos.add(destino);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(codigo, numPedidos);
                return params;
            }
        };

        queue.add(stringRequest);
    }

}
