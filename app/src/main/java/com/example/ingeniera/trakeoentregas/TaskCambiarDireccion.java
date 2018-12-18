package com.example.ingeniera.trakeoentregas;

import android.app.Activity;
import android.app.AlertDialog;
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
import com.example.ingeniera.trakeoentregas.Destino.Destinos;
import com.example.ingeniera.trakeoentregas.Destino.TaskCancerlarHojaDeRuta;
import com.example.ingeniera.trakeoentregas.Destino.TaskObtenerDatosRuta;
import com.example.ingeniera.trakeoentregas.Destino.Usuarios;
import com.example.ingeniera.trakeoentregas.Ingreso.SolicitarDestinos;
import com.example.ingeniera.trakeoentregas.Notificaciones.FirebaseMessagingService;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.ingeniera.trakeoentregas.Ingreso.SolicitarDestinos.almacenDestinos;

public class TaskCambiarDireccion extends AsyncTask<String,Void,String> {

    private ProgressDialog progreso;

    Context context;
    private int idDestino;
    public TaskCambiarDireccion(Context context,int idDestino) {
        this.context=context;
        this.idDestino=idDestino;

    }

    @Override
    protected void onPreExecute() {
        progreso=new ProgressDialog(context);
        progreso.setMessage("Verificando dirección");
        progreso.setCancelable(true);
        progreso.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                TaskCambiarDireccion.this.cancel(true);
            }
        });
        progreso.show();

    }

    @Override
    protected String doInBackground(String... strings) {
        String responseString="";
        try {
            responseString=requestDirection("https://maps.googleapis.com/maps/api/geocode/json?address="+strings[0]+"&key=AIzaSyAh43n9GmtYXG_Mr88OyNUJNltxQbYPftk");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseString;
    }


    private String requestDirection (String reqUrl) throws IOException {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            //Get the response
            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }
            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }
        return responseString;
    }


    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        //Toast.makeText(context,s,Toast.LENGTH_LONG).show();
        JSONObject jsonObject = null;
        JSONArray jResultados;
        JSONArray jGeometry;
        JSONArray jlocation;
        try {
            jsonObject=new JSONObject(s);
            String succed=jsonObject.optString("status");
            if (succed.equals("OK")){
                jResultados = jsonObject.getJSONArray("results");
                final Double lat = (Double) ((JSONObject)((JSONObject) ((JSONObject) jResultados.get(0)).get("geometry")).get("location")).get("lat");
                final Double lng = (Double) ((JSONObject)((JSONObject) ((JSONObject) jResultados.get(0)).get("geometry")).get("location")).get("lng");
                final String formatedAddress= (String) (((JSONObject) jResultados.get(0)).get("formatted_address"));
                progreso.dismiss();

                if (formatedAddress.matches(".*\\d+.*")){

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder
                            .setTitle("Cambiar por " + formatedAddress)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    TaskEnviarDireccion taskEnviarDireccion = new TaskEnviarDireccion(context,idDestino,lat,lng,formatedAddress);
                                    taskEnviarDireccion.execute();
                                }
                            })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }else {
                    SingleToast.show(context,"No se encuentra - Sea más específico",0);
                }

            }else {
                SingleToast.show(context,"No se encuentra la dirección",Toast.LENGTH_SHORT);
                progreso.dismiss();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            progreso.dismiss();
        }

    }

}

class TaskEnviarDireccion extends AsyncTask<String ,Void,String >{

    Context context;
    private int idDestino;
    private Double lat,lng;
    private ProgressDialog progreso;
    private String formatedAddress;
    public TaskEnviarDireccion(Context context,int idDestino,Double lat,Double lng,String formatedAddress) {
        this.context=context;
        this.idDestino=idDestino;
        this.lat=lat;
        this.lng=lng;
        this.formatedAddress=formatedAddress;
    }

    @Override
    protected void onPreExecute() {
        progreso=new ProgressDialog(context);
        progreso.setMessage("Enviando dirección...");
        progreso.setCancelable(true);
        progreso.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                TaskEnviarDireccion.this.cancel(true);
            }
        });
        progreso.show();
    }

    @Override
    protected String doInBackground(final String... strings) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://192.168.1.176/pruebas/prueba-remito-transporte/cambiar-direccion-transporte.php";

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
                            SingleToast.show(context,"Cambiado correctamente",Toast.LENGTH_SHORT);
                            ArrayList<Destinos> destinos=new ArrayList<>();
                            destinos=almacenDestinos.getArrayList("arrayDestinosKey");

                            for (int i = 0;i<destinos.size();i++){
                                if(destinos.get(i).getId()==idDestino){
                                    destinos.get(i).setLatitude(lat);
                                    destinos.get(i).setLongitude(lng);
                                    destinos.get(i).setDireccion(formatedAddress);
                                    almacenDestinos.saveArrayList(destinos);
                                }
                            }
                        }else {
                            progreso.dismiss();
                            SingleToast.show(context,"Error - "+mensaje,Toast.LENGTH_SHORT);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        SingleToast.show(context, "Error - "+e.toString(),Toast.LENGTH_SHORT);
                        progreso.dismiss();
                    }
                }else{
                    SingleToast.show(context,"Sin respuesta",Toast.LENGTH_SHORT);
                    progreso.dismiss();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                SingleToast.show(context,error.toString(),Toast.LENGTH_SHORT);
                progreso.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                String direccionParam= null;
                String transporteParam=null;
                String clienteParam=null;


                ArrayList<Destinos> destinos = almacenDestinos.getArrayList("arrayDestinosKey");
                for (int i = 0;i<destinos.size();i++){
                    if (idDestino==destinos.get(i).getId()){
                        if (destinos.get(i).getNombre_transporte().equals("SUCURSAL CLIENTE")){
                            clienteParam=destinos.get(i).getNombre_cliente();
                            transporteParam="";
                            break;
                        }else{
                            transporteParam=destinos.get(i).getNombre_transporte();
                            clienteParam="";
                            break;
                        }

                    }
                }
                Map<String, String> params = new HashMap<>();
                params.put("direccion", formatedAddress);
                params.put("latitud", String.valueOf(lat));
                params.put("longitud", String.valueOf(lng));
                params.put("transporte",transporteParam );
                params.put("cliente", clienteParam);
                ArrayList<Usuarios> usuarios=almacenDestinos.getArrayUsuarios("arrayUsuariosKey");
                for (int i=0;i<usuarios.size();i++) {
                    if (usuarios.get(i).getTipo().equals("Responsable")) {
                        params.put("responsable", usuarios.get(i).getDni());
                    }
                }
                return params;
            }
        };

        queue.add(stringRequest);

        return null;


    }

}



