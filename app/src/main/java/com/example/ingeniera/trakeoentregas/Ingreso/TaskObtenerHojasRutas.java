package com.example.ingeniera.trakeoentregas.Ingreso;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ingeniera.trakeoentregas.R;
import com.example.ingeniera.trakeoentregas.SingleToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.ingeniera.trakeoentregas.Ingreso.SolicitarDestinos.almacenDestinos;
import static com.example.ingeniera.trakeoentregas.Ingreso.SolicitarDestinos.urlSistemasAndifIP;

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
        progreso.setCancelable(false);
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
        //String url = "http://192.168.1.176/pruebas/prueba-remito-transporte/ultimas-hojas-ruta.php";
        String url = urlSistemasAndifIP+"/pruebas/prueba-remito-transporte/ultimas-hojas-ruta.php";

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
                            hojaDeRuta.setCantDestinos(jsonObjectExplorer.optInt("destinos"));
                            hojaDeRuta.setCantPendientes(jsonObjectExplorer.optInt("pendientes"));
                            hojasDeRutas.add(hojaDeRuta);
                        }
                        almacenDestinos.setArrayHojasDeRutas(hojasDeRutas);
                        if (context instanceof SolicitarDestinos&&almacenDestinos.getEstadoRuta()==1){
                            RecyclerView RecyclerviewSolDes = ((Activity) context).findViewById(R.id.RecyclerviewSolDes);
                            RecyclerviewSolDes.getAdapter().notifyDataSetChanged();
                            almacenDestinos.setEstadoRuta(1);
                        }else{
                            almacenDestinos.setEstadoRuta(1);
                            ((Activity)context).recreate();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        SingleToast.show(context, "Error - "+ e.toString(), Toast.LENGTH_SHORT);
                    }
                }else{
                    SingleToast.show(context, "Sin respuesta", Toast.LENGTH_SHORT);
                }
                progreso.dismiss();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                SingleToast.show(context, error.toString(), Toast.LENGTH_SHORT);
                progreso.dismiss();
            }
        });


        queue.add(stringRequest);

        return null;
    }
}
