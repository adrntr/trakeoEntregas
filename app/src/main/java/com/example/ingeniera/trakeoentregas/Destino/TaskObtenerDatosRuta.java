package com.example.ingeniera.trakeoentregas.Destino;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ingeniera.trakeoentregas.Ingreso.SolicitarDestinos;
import com.example.ingeniera.trakeoentregas.Notificaciones.FirebaseMessagingService;
import com.example.ingeniera.trakeoentregas.R;
import com.example.ingeniera.trakeoentregas.SingleToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.ingeniera.trakeoentregas.Ingreso.SolicitarDestinos.almacenDestinos;
import static com.example.ingeniera.trakeoentregas.Ingreso.SolicitarDestinos.estadoRuta;

/** A partir de un codigo de ruta, obtiene todos los destinos.
 *
 */

public class TaskObtenerDatosRuta extends AsyncTask<String,Void,String> {

    Context context=null;
    private ProgressDialog progreso;
    private LocalBroadcastManager broadcaster;

    public TaskObtenerDatosRuta(Context context) {
        this.context=context;
    }


    @Override
    protected void onPreExecute() {
        broadcaster = LocalBroadcastManager.getInstance(context);

        if (!(context instanceof FirebaseMessagingService)){
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



    }

    @Override
    protected String doInBackground(final String... strings) {

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://192.168.1.176/pruebas/prueba-remito-transporte/datos-hoja-ruta.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(!(jsonObject.getBoolean("error"))){
                        JSONArray jsonArray = jsonObject.getJSONArray("destinos");
                        Destinos destino;
                        ArrayList<Destinos> destinos = new ArrayList<>();
                        ArrayList<Destinos> destinosBackUp=almacenDestinos.getArrayDestinosBackUp("arrayDestinosKey");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            destino = new Destinos();
                            JSONObject jsonObjectExplorer = jsonArray.getJSONObject(i);
                            destino.setId(jsonObjectExplorer.optInt("id"));
                            destino.setId_externo(jsonObjectExplorer.optInt("id_externo"));
                            destino.setId_tipo_registro(jsonObjectExplorer.optInt("id_tipo_registro"));
                            destino.setOrden(jsonObjectExplorer.optInt("orden"));
                            destino.setEntregado(jsonObjectExplorer.optBoolean("entregado"));
                            destino.setNombre_tipo_registro(jsonObjectExplorer.optString("nombre_tipo_registro"));
                            destino.setId_cliente(jsonObjectExplorer.optInt("id_cliente"));//cambiar por id destino
                            destino.setNombre_cliente(jsonObjectExplorer.optString("nombre_cliente"));
                            destino.setCantidad(jsonObjectExplorer.optInt("cantidad"));
                            destino.setMotivo(jsonObjectExplorer.optString("motivo"));
                            destino.setNombre_transporte(jsonObjectExplorer.optString("nombre_transporte"));
                            destino.setDireccion_transporte(jsonObjectExplorer.optString("direccion_transporte"));
                            destino.setLatitude(jsonObjectExplorer.optDouble("latitud"));
                            destino.setLongitude(jsonObjectExplorer.optDouble("longitud"));
                            destino.setTelefono(jsonObjectExplorer.optString("telefono"));
                            destino.setCancelado(jsonObjectExplorer.optBoolean("entrega_cancelada"));
                            destino.setDireccion(jsonObjectExplorer.optString("direccion"));

                            Boolean agregadoDurRecorrido;
                            agregadoDurRecorrido=true;
                            if (destinosBackUp!=null) {
                                if (!(context instanceof SolicitarDestinos)) {
                                    for (int j = 0; j < destinosBackUp.size(); j++) {
                                        if (destino.getId() == destinosBackUp.get(j).getId() && !(destinosBackUp.get(j).getAgregadoDurRecorrido())) {//si lo encuentra y agregado
                                            agregadoDurRecorrido = false;
                                        }
                                    }
                                }else {
                                    agregadoDurRecorrido=false;
                                }
                            }else {
                                agregadoDurRecorrido=false;
                            }
                            destino.setAgregadoDurRecorrido(agregadoDurRecorrido);
                            if (destino.getLongitude() != 0 && destino.getLatitude() != 0) {
                                destinos.add(destino);
                            } else {
                                if (!(context instanceof FirebaseMessagingService)) {
                                    SingleToast.show(context, "El destino: " + destino.getNombre_transporte() + "No tiene latitudo y/o longitud", Toast.LENGTH_SHORT);
                                }
                            }
                        }
                        if (destinos.size() > 0) {
                            almacenDestinos.saveArrayList(destinos);
                            if (context instanceof SolicitarDestinos){
                                almacenDestinos.saveArrayDestinosBackUp(destinos);
                            }
                            almacenDestinos.setEstadoRuta(2);
                            almacenDestinos.setIdHojaDeRuta(strings[0]);
                            if (!(context instanceof FirebaseMessagingService)) {//si no fue llamada desde el servicio de la notificacion
                                progreso.dismiss();
                                if (context instanceof ListaDestinos) { //si fue llamado del listview solo actualiza
                                    RecyclerView recyclerView = ((Activity) context).findViewById(R.id.recyclerView);
                                    recyclerView.getAdapter().notifyDataSetChanged();
                                }else if (context instanceof MapsActivity){
                                    ((Activity)context).recreate();
                                }else{
                                    Intent intent = new Intent(context, ListaDestinos.class); //si fue llamada desde el registro para pasar de inicio a la hoja de ruta
                                    context.startActivity(intent);
                                }
                            }else {
                                Intent intent = new Intent("MyData"); //envia un intent a ListaDestinos para notificar que actualice la lista
                                broadcaster.sendBroadcast(intent);
                            }
                        } else {
                            if (!(context instanceof FirebaseMessagingService)) {
                                progreso.dismiss();
                                SingleToast.show(context, "No se encontraron los destinos - Vuelva a intentar", Toast.LENGTH_SHORT);
                            }


                        }
                    }else{
                        if (!(context instanceof FirebaseMessagingService)) {
                            progreso.dismiss();
                            SingleToast.show(context, jsonObject.optString("mensaje"), Toast.LENGTH_SHORT);

                        }
                    }
                } catch (JSONException e) {
                    if (!(context instanceof FirebaseMessagingService)) {
                        e.printStackTrace();
                        progreso.dismiss();
                    }
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!(context instanceof FirebaseMessagingService)) {
                    SingleToast.show(context, error.toString(), Toast.LENGTH_SHORT);
                    progreso.dismiss();
                    if (context instanceof SolicitarDestinos){
                        TaskCancerlarHojaDeRuta taskCancerlarHojaDeRuta= new TaskCancerlarHojaDeRuta(context);
                        taskCancerlarHojaDeRuta.execute(strings[0]);
                    }
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_ruta", strings[0]);
                params.put("token",almacenDestinos.getToken());
                return params;
            }
        };


        queue.add(stringRequest);

        return null;
    }


}
