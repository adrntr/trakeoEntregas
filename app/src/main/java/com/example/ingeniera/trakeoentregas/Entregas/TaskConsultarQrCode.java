package com.example.ingeniera.trakeoentregas.Entregas;

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
import com.example.ingeniera.trakeoentregas.Destinos;
import com.example.ingeniera.trakeoentregas.Ingreso.TaskObtenerHojasRutas;
import com.example.ingeniera.trakeoentregas.Ingreso.TaskValidarDNI;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.example.ingeniera.trakeoentregas.Ingreso.SolicitarDestinos.almacenDestinos;

public class TaskConsultarQrCode extends AsyncTask<String,Void,String> {

    Context context;
    private ProgressDialog progreso;
    ArrayList<Destinos> destinos;
    int i;
    public TaskConsultarQrCode(Context context, ArrayList<Destinos> destinos, int i) {
        this.context=context;
        this.destinos=destinos;
        this.i=i;

    }

    @Override
    protected void onPreExecute() {
        progreso=new ProgressDialog(context);
        progreso.setMessage("Realizando entrega...");
        progreso.setCancelable(true);
        progreso.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                TaskConsultarQrCode.this.cancel(true);
            }
        });
        progreso.show();
    }

    @Override
    protected String doInBackground(final String... strings) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://192.168.1.176/pruebas/prueba-remito-transporte/marcar-despachado.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response!=null){
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String tienePermiso=jsonObject.getString("error");
                        if(tienePermiso.equals("false")){
                            Toast.makeText(context,"Entregado Correctamente",Toast.LENGTH_SHORT).show();
                            progreso.dismiss();
                            destinos.get(i).setEntregado(true);
                            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                            destinos.get(i).setFechaHoraEntrega(currentDateTimeString);
                            almacenDestinos.saveArrayList(destinos);
                            Toast.makeText(context,"Entregado a las "+currentDateTimeString,Toast.LENGTH_SHORT).show();
                            //ingresoEt.setText("");
                        }else {
                            Toast.makeText(context,"No se pudo entregar",Toast.LENGTH_SHORT).show();
                            progreso.dismiss();
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
                params.put("id_externo", strings[0]);
                params.put("id_tipo_registro", strings[1]);
                params.put("responsable", almacenDestinos.getUsuario("dniKey"));

                return params;
            }
        };


        queue.add(stringRequest);

        return null;
    }

}
