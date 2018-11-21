package com.example.ingeniera.trakeoentregas;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.ingeniera.trakeoentregas.MapsActivity.almacenDestinos;


public class TaskObtenerDatosRuta extends AsyncTask<String,Void,String> {

    Context context;
    GoogleMap mMap;
    private ProgressDialog progreso;

    public TaskObtenerDatosRuta(Context context,GoogleMap mMap) {
        this.context=context;
        this.mMap=mMap;
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
        String url = "https://sistemas.andif.com.ar/pruebas/prueba-remito-transporte/datos-planilla-seguro.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(context, response, Toast.LENGTH_LONG).show();

                onPostExecute(response);
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

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if(s!=null){
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("registros");
                Destinos destino;
                ArrayList<Destinos> destinos = new ArrayList<>();
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
                    MarkerOptions markerOptions2 = new MarkerOptions();
                    markerOptions2.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    LatLng latLng2= new LatLng(destino.getLatitude(),destino.getLongitude());
                    markerOptions2.position(latLng2);
                    mMap.addMarker(markerOptions2);
                    destinos.add(destino);
                }
                almacenDestinos.saveArrayList(destinos);
                DireccionesMapsApi direccionesMapsApi = new DireccionesMapsApi(mMap,context);
                direccionesMapsApi.getRequestedUrl(destinos);
                progreso.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }



}
