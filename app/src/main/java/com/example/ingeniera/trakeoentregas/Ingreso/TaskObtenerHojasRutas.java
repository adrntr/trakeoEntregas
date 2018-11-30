package com.example.ingeniera.trakeoentregas.Ingreso;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.ingeniera.trakeoentregas.Ingreso.SolicitarDestinos.almacenDestinos;

public class TaskObtenerHojasRutas extends AsyncTask<String,Void,String> {



    Context context;
    private ProgressDialog progreso;

    public TaskObtenerHojasRutas(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        progreso=new ProgressDialog(context);
        progreso.setMessage("Obteniendo hojas de ruta...");
        progreso.setCancelable(true);
        progreso.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                TaskObtenerHojasRutas.this.cancel(true);
            }
        });
        progreso.show();
    }
    @Override
    protected String doInBackground(final String... strings) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://192.168.1.176/pruebas/prueba-remito-transporte/ultimas-hojas-ruta.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response!=null){
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("registros");
                        HojasDeRuta hojaDeRuta;
                        ArrayList<HojasDeRuta> hojasDeRutas = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            hojaDeRuta = new HojasDeRuta();
                            JSONObject jsonObjectExplorer = jsonArray.getJSONObject(i);
                            hojaDeRuta.setCodigo(jsonObjectExplorer.optInt("id"));
                            hojaDeRuta.setFecha(jsonObjectExplorer.optString("fecha_ruta"));
                            hojasDeRutas.add(hojaDeRuta);
                        }
                        almacenDestinos.setArrayHojasDeRutas(hojasDeRutas);
                        almacenDestinos.setEstadoRuta(1);
                        progreso.dismiss();
                        Intent intent = new Intent(context,SolicitarDestinos.class);
                        context.startActivity(intent);
                        ((Activity)context).finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(context,"Sin respuesta",Toast.LENGTH_SHORT).show();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });


        queue.add(stringRequest);

        return null;
    }
}
