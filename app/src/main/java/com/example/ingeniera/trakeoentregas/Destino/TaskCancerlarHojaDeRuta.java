package com.example.ingeniera.trakeoentregas.Destino;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ingeniera.trakeoentregas.Entregas.TaskConsultarQrCode;
import com.example.ingeniera.trakeoentregas.Ingreso.SolicitarDestinos;
import com.example.ingeniera.trakeoentregas.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.example.ingeniera.trakeoentregas.Ingreso.SolicitarDestinos.almacenDestinos;

public class TaskCancerlarHojaDeRuta extends AsyncTask<String ,Void,String > {

    Context context;
    private ProgressDialog progreso;

    public TaskCancerlarHojaDeRuta(Context context) {
            this.context=context;
        }

        @Override
        protected void onPreExecute() {
            progreso=new ProgressDialog(context);
            progreso.setMessage("Finalizando hoja de ruta...");
            progreso.setCancelable(true);
            progreso.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    TaskCancerlarHojaDeRuta.this.cancel(true);
                }
            });
            progreso.show();
        }

        @Override
        protected String doInBackground(final String... strings) {
            RequestQueue queue = Volley.newRequestQueue(context);
            String url = "http://192.168.1.176/pruebas/prueba-remito-transporte/marcar-en-curso.php";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    if(response!=null){
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String error=jsonObject.getString("error");
                            String mensaje= jsonObject.getString("mensaje");
                            if (error.equals("false")){
                                progreso.dismiss();
                                Toast.makeText(context,"Finalizado Correctamente",Toast.LENGTH_SHORT).show();

                                if(almacenDestinos.getComenzoRecorrido()){
                                    almacenDestinos.setEstadoRuta(0);
                                    almacenDestinos.setComenzoRecorrido(false);
                                } else {
                                    almacenDestinos.setEstadoRuta(1);
                                }
                                Intent intent=new Intent(context,SolicitarDestinos.class);
                                context.startActivity(intent);
                                ((Activity)context).finish();
                            }else {
                                progreso.dismiss();
                                Toast.makeText(context,"Volver a intentar",Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Error - Intente nuevamente",Toast.LENGTH_SHORT).show();
                            progreso.dismiss();
                        }
                    }else{
                        Toast.makeText(context,"Sin respuesta",Toast.LENGTH_SHORT).show();
                    }

                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context,error.toString(),Toast.LENGTH_SHORT).show();
                    progreso.dismiss();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("id_ruta", strings[0]);
                    params.put("en_curso", "0");
                    return params;
                }
            };

            queue.add(stringRequest);

            return null;


    }

}
