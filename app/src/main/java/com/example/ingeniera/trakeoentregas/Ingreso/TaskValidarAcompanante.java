package com.example.ingeniera.trakeoentregas.Ingreso;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ingeniera.trakeoentregas.Destino.Usuarios;
import com.example.ingeniera.trakeoentregas.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.ingeniera.trakeoentregas.Ingreso.SolicitarDestinos.almacenDestinos;

public class TaskValidarAcompanante extends AsyncTask<String,Void,String> {

    Context context;
    private ProgressDialog progreso;

    public TaskValidarAcompanante(Context context) {
        this.context=context;
    }

    @Override
    protected void onPreExecute() {
        progreso=new ProgressDialog(context);
        progreso.setMessage("Verificando su DNI...");
        progreso.setCancelable(true);
        progreso.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                TaskValidarAcompanante.this.cancel(true);
            }
        });
        progreso.show();
    }

    @Override
    protected String doInBackground(final String... strings) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "https://sistemas.andif.com.ar/pruebas/prueba-remito-transporte/logueo.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response!=null){
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String tienePermiso=jsonObject.getString("tiene_permiso");
                        if(tienePermiso.equals("true")){
                            String nombreApellido=jsonObject.getString("nombre_colaborador");
                            Toast.makeText(context,"Bienvenido Sr/Sra "+nombreApellido,Toast.LENGTH_SHORT).show();
                            ArrayList<Usuarios> acompañantes =almacenDestinos.getArrayUsuarios("arrayUsuariosKey");
                            if (acompañantes==null){
                                acompañantes=new ArrayList<>();
                            }
                            Usuarios acompañante = new Usuarios(nombreApellido,strings[0],"Acompañante");
                            acompañantes.add(acompañante);
                            almacenDestinos.setArrayUsuarios(acompañantes);
                            progreso.dismiss();
                            ((Activity)context).recreate();
                            ((Activity)context).overridePendingTransition(0, 0);

                        }else {
                            Toast.makeText(context,"No se encuentra su DNI",Toast.LENGTH_SHORT).show();
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
                Toast.makeText(context,error.toString(),Toast.LENGTH_SHORT).show();
                progreso.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("dni", strings[0]);
                return params;
            }
        };


        queue.add(stringRequest);

        return null;
    }

}
