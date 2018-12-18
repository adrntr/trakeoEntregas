package com.example.ingeniera.trakeoentregas.Destino;

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
import com.example.ingeniera.trakeoentregas.Ingreso.SolicitarDestinos;
import com.example.ingeniera.trakeoentregas.R;
import com.example.ingeniera.trakeoentregas.SingleToast;
import com.example.ingeniera.trakeoentregas.TransporteInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.ingeniera.trakeoentregas.Ingreso.SolicitarDestinos.almacenDestinos;

public class TaskCancelarDestino extends AsyncTask<String,Void,String> {
    Context context;
    private ProgressDialog progreso;

    public TaskCancelarDestino(Context context) {
        this.context=context;
    }

    @Override
    protected void onPreExecute() {
        progreso=new ProgressDialog(context);
        progreso.setCancelable(true);
        progreso.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                TaskCancelarDestino.this.cancel(true);
            }
        });
        progreso.show();
    }

    @Override
    protected String doInBackground(final String... strings) {
        if (strings[1].equals("1")){
            progreso.setMessage("Cancelando destino");
        }else {
            progreso.setMessage("Activando destino");
        }
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://192.168.1.176/pruebas/prueba-remito-transporte/cancelar-entrega.php";

        final StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response!=null){
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String error=jsonObject.getString("error");
                        String mensaje= jsonObject.getString("mensaje");
                        if (error.equals("false")){
                            progreso.dismiss();
                            ArrayList<Destinos> destinos=almacenDestinos.getArrayList("arrayDestinosKey");
                            for (int i = 0;i<destinos.size();i++){
                                if (destinos.get(i).getId()==Integer.valueOf(strings[0])){
                                    if (Integer.valueOf(strings[1])==0){
                                        destinos.get(i).setCancelado(false);
                                        break;
                                    }else {
                                        destinos.get(i).setCancelado(true);
                                        break;
                                    }
                                }
                            }
                            almacenDestinos.saveArrayList(destinos);


                            if (context instanceof ListaDestinos){
                                RecyclerView recyclerView = ((Activity) context).findViewById(R.id.recyclerView);
                                recyclerView.getAdapter().notifyDataSetChanged();
                            }else if (context instanceof TransporteInfo){
                                ((Activity)context).recreate();
                            }
                            //SingleToast.show(context,"Realizado con éxito",Toast.LENGTH_SHORT);


                        }else {
                            progreso.dismiss();
                            SingleToast.show(context,"Error "+ mensaje,Toast.LENGTH_SHORT);
                            if (context instanceof ListaDestinos){
                                RecyclerView recyclerView = ((Activity) context).findViewById(R.id.recyclerView);
                                recyclerView.getAdapter().notifyDataSetChanged();
                            }else if (context instanceof TransporteInfo){
                                ((Activity)context).recreate();
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        SingleToast.show(context, "Error - Intente nuevamente",Toast.LENGTH_SHORT);
                        progreso.dismiss();
                        if (context instanceof ListaDestinos){
                            RecyclerView recyclerView = ((Activity) context).findViewById(R.id.recyclerView);
                            recyclerView.getAdapter().notifyDataSetChanged();
                        }else if (context instanceof TransporteInfo){
                            ((Activity)context).recreate();
                        }
                    }
                }else{
                    progreso.dismiss();
                    SingleToast.show(context,"Sin respuesta",Toast.LENGTH_SHORT);
                    if (context instanceof ListaDestinos){
                        RecyclerView recyclerView = ((Activity) context).findViewById(R.id.recyclerView);
                        recyclerView.getAdapter().notifyDataSetChanged();
                    } else if (context instanceof TransporteInfo){
                        ((Activity)context).recreate();
                    }
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                SingleToast.show(context,error.toString(),Toast.LENGTH_SHORT);
                progreso.dismiss();
                if (context instanceof ListaDestinos){
                    RecyclerView recyclerView = ((Activity) context).findViewById(R.id.recyclerView);
                    recyclerView.getAdapter().notifyDataSetChanged();
                }else if (context instanceof TransporteInfo){
                    ((Activity)context).recreate();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_registro", strings[0]);
                params.put("cancelar", strings[1]);
                return params;
            }
        };

        queue.add(stringRequest);

        return null;


    }




}
