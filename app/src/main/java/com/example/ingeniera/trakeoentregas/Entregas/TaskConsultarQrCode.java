package com.example.ingeniera.trakeoentregas.Entregas;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ingeniera.trakeoentregas.Destino.Destinos;
import com.example.ingeniera.trakeoentregas.Destino.Usuarios;
import com.example.ingeniera.trakeoentregas.R;
import com.example.ingeniera.trakeoentregas.SingleToast;
import com.example.ingeniera.trakeoentregas.TransporteInfo;

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
            @SuppressLint("RestrictedApi")
            @Override
            public void onResponse(String response) {

                if(response!=null){
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String error=jsonObject.getString("error");
                        String mensaje= jsonObject.getString("mensaje");

                        if (strings[2].equals("1")){ //Se entrego
                            if(error.equals("false")){
                                SingleToast.show(context,"Entregado Correctamente",Toast.LENGTH_SHORT);
                                progreso.dismiss();
                                destinos.get(i).setEntregado(true);
                                String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                                destinos.get(i).setFechaHoraEntrega(currentDateTimeString);
                                almacenDestinos.saveArrayList(destinos);

                                if (context instanceof TransporteInfo){
                                    FloatingActionButton entregarTransporteInfoFb = ((Activity)context).findViewById(R.id.entregarTransporteInfoFb);
                                    FloatingActionButton irTransporteInfoFb = ((Activity)context).findViewById(R.id.irTransporteInfoFb);
                                    FloatingActionButton cancelarEntregaBt= ((Activity)context).findViewById(R.id.cancelarEntregaBt);
                                    TextView entregadoTv =  ((Activity)context).findViewById(R.id.entregadoTv);
                                    TextView irTransporteInfoTv=((Activity)context).findViewById(R.id.irTransporteInfoTv);
                                    TextView entregarTransporteInfoTv=((Activity)context).findViewById(R.id.entregarTransporteInfoTv);
                                    TextView entregadoTv3=((Activity)context).findViewById(R.id.entregadoTv3);
                                    TextView cancelarEntregaTransporteInfoTv = ((Activity)context).findViewById(R.id.cancelarEntregaTransporteInfoTv);
                                    Switch switchAB=((Activity)context).findViewById(R.id.switchAB);
                                    entregarTransporteInfoFb.setVisibility(View.GONE);
                                    irTransporteInfoFb.setVisibility(View.GONE);
                                    cancelarEntregaBt.setVisibility(View.VISIBLE);
                                    entregadoTv.setVisibility(View.VISIBLE);
                                    entregadoTv3.setVisibility(View.VISIBLE);
                                    entregadoTv.setText(destinos.get(i).getFechaHoraEntrega());
                                    irTransporteInfoTv.setVisibility(View.GONE);
                                    entregarTransporteInfoTv.setVisibility(View.GONE);
                                    cancelarEntregaTransporteInfoTv.setVisibility(View.VISIBLE);
                                    switchAB.setVisibility(View.GONE);
                                }

                            }else if(mensaje.equals("Ya se ha marcado como despachado")) {
                                destinos.get(i).setEntregado(true);
                                almacenDestinos.saveArrayList(destinos);
                                SingleToast.show(context,mensaje,Toast.LENGTH_SHORT);
                                progreso.dismiss();
                            }else {
                                SingleToast.show(context,mensaje,Toast.LENGTH_SHORT);
                                progreso.dismiss();
                            }
                        }else{
                            if(error.equals("false")){
                                SingleToast.show(context,"Cancelado Correctamente",Toast.LENGTH_SHORT);
                                progreso.dismiss();
                                destinos.get(i).setEntregado(false);
                                destinos.get(i).setFechaHoraEntrega("");
                                almacenDestinos.saveArrayList(destinos);
                                almacenDestinos.saveArrayList(destinos);
                                FloatingActionButton entregarTransporteInfoFb = ((Activity)context).findViewById(R.id.entregarTransporteInfoFb);
                                FloatingActionButton irTransporteInfoFb = ((Activity)context).findViewById(R.id.irTransporteInfoFb);
                                FloatingActionButton cancelarEntregaBt= ((Activity)context).findViewById(R.id.cancelarEntregaBt);
                                TextView entregadoTv =  ((Activity)context).findViewById(R.id.entregadoTv);
                                TextView irTransporteInfoTv=((Activity)context).findViewById(R.id.irTransporteInfoTv);
                                TextView entregarTransporteInfoTv=((Activity)context).findViewById(R.id.entregarTransporteInfoTv);
                                TextView entregadoTv3=((Activity)context).findViewById(R.id.entregadoTv3);
                                TextView cancelarEntregaTransporteInfoTv = ((Activity)context).findViewById(R.id.cancelarEntregaTransporteInfoTv);
                                Switch switchAB=((Activity)context).findViewById(R.id.switchAB);
                                entregarTransporteInfoFb.setVisibility(View.VISIBLE);
                                irTransporteInfoFb.setVisibility(View.VISIBLE);
                                cancelarEntregaBt.setVisibility(View.GONE);
                                entregadoTv.setVisibility(View.GONE);
                                entregadoTv3.setVisibility(View.GONE);
                                irTransporteInfoTv.setVisibility(View.VISIBLE);
                                cancelarEntregaTransporteInfoTv.setVisibility(View.GONE);
                                entregarTransporteInfoTv.setVisibility(View.VISIBLE);
                                switchAB.setVisibility(View.VISIBLE);
                            }else if(mensaje.equals("No se ha marcado como despachado")) {
                                destinos.get(i).setEntregado(false);
                                almacenDestinos.saveArrayList(destinos);
                                SingleToast.show(context,mensaje,Toast.LENGTH_SHORT);
                                //Button cancelarEntregaBt = ((Activity)context).findViewById(R.id.cancelarEntregaBt);
                                //cancelarEntregaBt.setVisibility(View.GONE);
                                progreso.dismiss();
                            }else {
                                SingleToast.show(context,mensaje,Toast.LENGTH_SHORT);
                                progreso.dismiss();
                            }
                        }



                    } catch (JSONException e) {
                        e.printStackTrace();
                        SingleToast.show(context, "Error - Intente nuevamente",Toast.LENGTH_SHORT);
                        progreso.dismiss();
                    }
                }else{
                    progreso.dismiss();
                    SingleToast.show(context,"Sin respuesta",Toast.LENGTH_SHORT);
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
                Map<String, String> params = new HashMap<>();
                params.put("id_externo", strings[0]);
                params.put("id_tipo_registro", strings[1]);
                params.put("entregado",strings[2]);
                params.put("id",strings[3]);
                ArrayList<Usuarios> usuarios=almacenDestinos.getArrayUsuarios("arrayUsuariosKey");
                ArrayList<String> dnisAcompañantes=new ArrayList<>();
                int numAcompañante=0;
                for (int i=0;i<usuarios.size();i++){
                    if (usuarios.get(i).getTipo().equals("Responsable")){
                        params.put("responsable", usuarios.get(i).getDni());
                    }else {
                        params.put("acompanantes_"+numAcompañante,usuarios.get(i).getDni());
                        numAcompañante++;
                    }
                }
                return params;
            }
        };


        queue.add(stringRequest);

        return null;
    }

}
