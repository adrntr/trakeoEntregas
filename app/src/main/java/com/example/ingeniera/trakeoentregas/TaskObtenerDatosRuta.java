package com.example.ingeniera.trakeoentregas;

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
import java.util.HashMap;
import java.util.Map;

import static com.example.ingeniera.trakeoentregas.Ingreso.SolicitarDestinos.almacenDestinos;

/** A partir de un codigo de ruta, obtiene todos los destinos.
 *
 */

public class TaskObtenerDatosRuta extends AsyncTask<String,Void,String> {

    Context context;
    private ProgressDialog progreso;

    public TaskObtenerDatosRuta(Context context) {
        this.context=context;
    }

    @Override
    protected void onPreExecute() {
        progreso=new ProgressDialog(context);
        progreso.setMessage("Cargando Ruta...");
        progreso.setCancelable(true);
        progreso.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                TaskObtenerDatosRuta.this.cancel(true);
            }
        });
        progreso.show();
    }

    @Override
    protected String doInBackground(final String... strings) {

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://192.168.1.176/pruebas/prueba-remito-transporte/datos-hoja-ruta.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response!=null){
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("destinos");
                        Destinos destino;
                        ArrayList<Destinos> destinos = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            destino = new Destinos();
                            JSONObject jsonObjectExplorer = jsonArray.getJSONObject(i);
                            destino.setId(jsonObjectExplorer.optInt("id"));
                            destino.setId_externo(jsonObjectExplorer.optInt("id_externo"));
                            destino.setId_tipo_registro(jsonObjectExplorer.optInt("id_tipo_registro"));
                            destino.setOrden(jsonObjectExplorer.optInt("orden"));
                            destino.setId_cliente(jsonObjectExplorer.optInt("id_cliente"));//cambiar por id destino
                            destino.setNombre_cliente(jsonObjectExplorer.optString("nombre_cliente"));
                            destino.setCantidad(jsonObjectExplorer.optInt("cantidad"));
                            destino.setMotivo(jsonObjectExplorer.optString("motivo"));
                            destino.setNombre_transporte(jsonObjectExplorer.optString("nombre_transporte"));
                            destino.setDireccion_transporte(jsonObjectExplorer.optString("direccion_transporte"));
                            destino.setLatitude(jsonObjectExplorer.optDouble("latitud"));
                            destino.setLongitude(jsonObjectExplorer.optDouble("longitud"));


                            destino.setEntregado(false);
                            if(destino.getLongitude()!=0&&destino.getLatitude()!=0){
                                destinos.add(destino);
                            }
                        }
                        if (destinos.size()>0){
                            almacenDestinos.saveArrayList(destinos);
                            almacenDestinos.setEstadoRuta(2);
                            progreso.dismiss();
                            Intent intent = new Intent(context,ListaDestinos.class);
                            context.startActivity(intent);
                        }else {
                            progreso.dismiss();
                            Toast.makeText(context,"No se encontraron los destinos",Toast.LENGTH_SHORT).show();
                            Toast.makeText(context,"Vuelva a intentar",Toast.LENGTH_SHORT).show();


                        }





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
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(strings[0], strings[1]);
                return params;
            }
        };


        queue.add(stringRequest);

        return null;
    }


}
