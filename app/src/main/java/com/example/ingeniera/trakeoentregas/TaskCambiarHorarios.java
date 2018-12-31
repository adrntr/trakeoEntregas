package com.example.ingeniera.trakeoentregas;

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
import com.example.ingeniera.trakeoentregas.Destino.Destinos;
import com.example.ingeniera.trakeoentregas.Destino.Usuarios;
import com.example.ingeniera.trakeoentregas.Ingreso.TaskObtenerHojasRutas;
import com.example.ingeniera.trakeoentregas.Ingreso.TaskValidarDNI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.ingeniera.trakeoentregas.Ingreso.SolicitarDestinos.almacenDestinos;
import static com.example.ingeniera.trakeoentregas.Ingreso.SolicitarDestinos.urlSistemasAndifIP;

public class TaskCambiarHorarios extends AsyncTask<String,Void,String> {

    Context context;
    private ProgressDialog progreso;

    public TaskCambiarHorarios(Context context) {
        this.context=context;
    }

    @Override
    protected void onPreExecute() {
        progreso=new ProgressDialog(context);
        progreso.setMessage("Actualizando horarios...");
        progreso.setCancelable(false);
        progreso.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                TaskCambiarHorarios.this.cancel(true);
            }
        });
        progreso.show();
    }

    @Override
    protected String doInBackground(final String... strings) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = urlSistemasAndifIP+"/pruebas/prueba-remito-transporte/cambiar-horario-transporte.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response!=null){
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Boolean error=jsonObject.getBoolean("error");
                        String mensaje=jsonObject.getString("mensaje");
                        if(!error){
                            ArrayList<Destinos> destinos=almacenDestinos.getArrayList("arrayDestinosKey");
                            for (int i = 0;i<destinos.size();i++){
                                if (Integer.parseInt(strings[4])==destinos.get(i).getId()){
                                    destinos.get(i).sethorario1_inicio(strings[0]);
                                    destinos.get(i).sethorario1_fin(strings[1]);
                                    destinos.get(i).sethorario2_incio(strings[2]);
                                    destinos.get(i).sethorario2_fin(strings[3]);
                                    }
                                }
                            almacenDestinos.saveArrayList(destinos);
                            SingleToast.show(context,"Actualizado Correctamente",1);
                        }else {
                            SingleToast.show(context, mensaje, Toast.LENGTH_SHORT);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        SingleToast.show(context,e.toString(),1);
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
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("aperturas_0", strings[0]);
                params.put("cierres_0", strings[1]);
                params.put("aperturas_1", strings[2]);
                params.put("cierres_1", strings[3]);
                params.put("id_transporte", strings[5]);
                params.put("id_cliente",strings[6]);

                return params;
            }
        };


        queue.add(stringRequest);

        return null;
    }
}
