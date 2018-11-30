package com.example.ingeniera.trakeoentregas.Entregas;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.example.ingeniera.trakeoentregas.Destino.Destinos;
import com.example.ingeniera.trakeoentregas.R;

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
                        String error=jsonObject.getString("error");
                        String mensaje= jsonObject.getString("mensaje");

                        if (strings[2].equals("1")){
                            if(error.equals("false")){
                                Toast.makeText(context,"Entregado Correctamente",Toast.LENGTH_SHORT).show();
                                progreso.dismiss();
                                destinos.get(i).setEntregado(true);
                                String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                                destinos.get(i).setFechaHoraEntrega(currentDateTimeString);
                                almacenDestinos.saveArrayList(destinos);
                                Toast.makeText(context,"Entregado a las "+currentDateTimeString,Toast.LENGTH_SHORT).show();
                                EditText ingresoEt = ((Activity)context).findViewById(R.id.ingresoEt);
                                ingresoEt.setText("");
                            }else if(mensaje.equals("Ya se ha marcado como despachado")) {
                                destinos.get(i).setEntregado(true);
                                almacenDestinos.saveArrayList(destinos);
                                Toast.makeText(context,mensaje,Toast.LENGTH_SHORT).show();
                                progreso.dismiss();
                            }else {
                                Toast.makeText(context,mensaje,Toast.LENGTH_SHORT).show();
                                progreso.dismiss();
                            }
                        }else{
                            if(error.equals("false")){
                                Toast.makeText(context,"Cancelado Correctamente",Toast.LENGTH_SHORT).show();
                                progreso.dismiss();
                                destinos.get(i).setEntregado(false);
                                destinos.get(i).setFechaHoraEntrega("");
                                almacenDestinos.saveArrayList(destinos);
                                almacenDestinos.saveArrayList(destinos);
                                Button cancelarEntregaBt = ((Activity)context).findViewById(R.id.cancelarEntregaBt);
                                cancelarEntregaBt.setVisibility(View.GONE);
                                Toast.makeText(context,"Entrega cancelada",Toast.LENGTH_SHORT).show();
                            }else if(mensaje.equals("No se ha marcado como despachado")) {
                                destinos.get(i).setEntregado(false);
                                almacenDestinos.saveArrayList(destinos);
                                Toast.makeText(context,mensaje,Toast.LENGTH_SHORT).show();
                                Button cancelarEntregaBt = ((Activity)context).findViewById(R.id.cancelarEntregaBt);
                                cancelarEntregaBt.setVisibility(View.GONE);
                                progreso.dismiss();
                            }else {
                                Toast.makeText(context,mensaje,Toast.LENGTH_SHORT).show();
                                progreso.dismiss();
                            }
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

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_externo", strings[0]);
                params.put("id_tipo_registro", strings[1]);
                params.put("entregado",strings[2]);
                params.put("responsable", almacenDestinos.getUsuario("dniKey"));

                return params;
            }
        };


        queue.add(stringRequest);

        return null;
    }

}
