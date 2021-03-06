package com.example.ingeniera.trakeoentregas.Ingreso;

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
import com.example.ingeniera.trakeoentregas.Destino.Usuarios;
import com.example.ingeniera.trakeoentregas.SingleToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.ingeniera.trakeoentregas.Ingreso.SolicitarDestinos.almacenDestinos;
import static com.example.ingeniera.trakeoentregas.Ingreso.SolicitarDestinos.urlSistemasAndifIP;

public class TaskValidarDNI extends AsyncTask<String,Void,String> {

    Context context;
    private ProgressDialog progreso;

    public TaskValidarDNI(Context context) {
        this.context=context;
    }

    @Override
    protected void onPreExecute() {
        progreso=new ProgressDialog(context);
        progreso.setMessage("Verificando su DNI...");
        progreso.setCancelable(false);
        progreso.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                TaskValidarDNI.this.cancel(true);
            }
        });
        progreso.show();
    }

    @Override
    protected String doInBackground(final String... strings) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = urlSistemasAndifIP+"/pruebas/prueba-remito-transporte/logueo.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response!=null){
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String tienePermiso=jsonObject.getString("tiene_permiso");
                        if(tienePermiso.equals("true")){
                            String nombreApellido=jsonObject.getString("nombre_colaborador");
                            SingleToast.show(context, "Bienvenido Sr/Sra "+nombreApellido, Toast.LENGTH_SHORT);
                            ArrayList<Usuarios> usuarios = new ArrayList<>();
                            Usuarios responsable = new Usuarios(nombreApellido,strings[0],"Responsable");
                            usuarios.add(responsable);
                            almacenDestinos.setArrayUsuarios(usuarios);
                            TaskObtenerHojasRutas obtenerHojasRutas = new TaskObtenerHojasRutas(context);
                            obtenerHojasRutas.execute();
                        }else {
                            SingleToast.show(context, "No se encuentra su DNI", Toast.LENGTH_SHORT);
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
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
                params.put("dni", strings[0]);
                return params;
            }
        };


        queue.add(stringRequest);

        return null;
    }

}
