package com.example.ingeniera.trakeoentregas.Destino;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.example.ingeniera.trakeoentregas.DirectionsParser;
import com.example.ingeniera.trakeoentregas.Entregas.QrReader;
import com.example.ingeniera.trakeoentregas.R;
import com.example.ingeniera.trakeoentregas.SingleToast;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.example.ingeniera.trakeoentregas.Ingreso.SolicitarDestinos.almacenDestinos;

public class DireccionesMapsApi {

    GoogleMap mMap;
    Context context;
    private static final String latAndif="-34.673841",lngAndif="-58.575378";

    public DireccionesMapsApi(GoogleMap mMap, Context context) {
        this.mMap=mMap;
        this.context=context;
    }
    public DireccionesMapsApi() {
    }
    public DireccionesMapsApi(Context context) {
        this.context=context;
    }

    public void getRequestedUrl(ArrayList<Destinos> destinos, Boolean pedirDestinos) {
        ArrayList<LatLng> waypoints =new ArrayList<>();

        for(int i=0;i<destinos.size();i++){
            if(destinos.get(i).getLatitude()!=0&&destinos.get(i).getLongitude()!=0) {
                waypoints.add(new LatLng(destinos.get(i).getLatitude(), destinos.get(i).getLongitude()));
            }
        }
        String waypointsStr="",str_org = null,str_dest=null;
        if(pedirDestinos){
            for (int i=0;i<waypoints.size()&&i<22;i++){
                if(i==0){
                    waypointsStr+=waypoints.get(i).latitude+","+waypoints.get(i).longitude;
                }else{
                    waypointsStr+="|"+waypoints.get(i).latitude+","+waypoints.get(i).longitude;
                }
            }
        }else {
            Boolean primero=true;
            int cant=0;
            for (int i = 0;i<waypoints.size()&& cant < 9; i++) {
                if (!(destinos.get(i).getEntregado())&&!(destinos.get(i).getCancelado())){
                    if (primero) {
                        waypointsStr += waypoints.get(i).latitude + "," + waypoints.get(i).longitude;
                        primero=false;
                        cant++;
                    } else {
                        waypointsStr += "|" + waypoints.get(i).latitude + "," + waypoints.get(i).longitude;
                        cant++;
                    }
                }
            }
        }
        str_org = "origin="+almacenDestinos.getLat()+","+almacenDestinos.getLng();
        str_dest = "destination="+latAndif+","+lngAndif;
        String sensor="sensor=false";
        //mode for find direction
        String mode= "mode=driving";
        //Waypoints
        String param = str_org+"&"+str_dest+"&"+sensor+"&"+mode+"&"+"waypoints="+waypointsStr;
        String paramMaps=str_org+"&"+str_dest+"&"+sensor+"&"+mode+"&"+"waypoints="+waypointsStr;
        String output = "json";
        //create url to request
        //String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+param+"&key=AIzaSyAh43n9GmtYXG_Mr88OyNUJNltxQbYPftk";
        String urlGoogleMaps="https://www.google.com/maps/dir/?api=1&"+paramMaps;
        almacenDestinos.setUrlGoogleMaps(urlGoogleMaps);

        /*
        if(pedirDestinos){
            TaskRequestDirections taskRequestDirections=new TaskRequestDirections();
            taskRequestDirections.execute(url);
        }
        */


    }

    /*ESTO ES PARA OBTENER LAS LINEAS Y LA INFORMACION DEL RECORRIDO (SI SON MAS DE 23 NO ENTREGA NADA)

    public class TaskRequestDirections extends AsyncTask<String,Void,String> {


        @Override
        protected String doInBackground(String... strings) {
            String responseString="";
            try {
                responseString=requestDirection(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //parse json
            TaskParser taskParser=new TaskParser();
            taskParser.execute(s);
        }
    }

    //a partir de una url obtiene los datos de como llegar al destino
    private String requestDirection(String reqUrl) throws IOException {
        String responseString="";
        InputStream inputStream=null;
        HttpURLConnection httpURLConnection = null;
        try{
            URL url=new URL(reqUrl);
            httpURLConnection=(HttpURLConnection)url.openConnection();
            httpURLConnection.connect();

            //Get the response
            inputStream=httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader=new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line="";
            while ((line=bufferedReader.readLine())!=null){
                stringBuffer.append(line);
            }

            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (inputStream!=null){
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }

        return responseString;
    }

    public class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>>>{

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject=null;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jsonObject=new JSONObject(strings[0]);
                DirectionsParser directionsParser=new DirectionsParser();
                routes = directionsParser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            //get list route and display it into the map

            almacenDestinos.saveArrayListPuntos(lists);
            almacenDestinos.setEstadoRuta(3);

            agregarLineas();


        }
    }

    public void agregarLineas() {
        List<List<HashMap<String, String>>> lists = almacenDestinos.getArrayListPuntos("arrayPuntosKey");
        ArrayList points = null;
        if (lists == null){
            almacenDestinos.setEstadoRuta(0);
            return;
        }
        if(lists.size()==0){
            //getRequestedUrl(almacenDestinos.getArrayList("arrayDestinosKey"),true);
        }else {
            for (List<HashMap<String, String>> path : lists) {
                points = new ArrayList();
                for (HashMap<String, String> point : path) {
                    double lat = Double.parseDouble(point.get("lat"));
                    double lon = Double.parseDouble(point.get("lon"));
                    points.add(new LatLng(lat, lon));
                }

                if (points != null) {
                    mMap.addPolyline(new PolylineOptions()
                            .color(Color.BLUE)
                            .width(3)
                            .addAll(points));

                } else {
                    SingleToast.show(context, "Direction not found", Toast.LENGTH_SHORT);
                }
            }
        }

    }

    */
    public void agregarMarkers() {

        ArrayList<Destinos> destinos = almacenDestinos.getArrayList("arrayDestinosKey");
        if (destinos!=null&&mMap!=null){

        for (int i = 0; i < destinos.size(); i++) {

            if (destinos.get(i).getEntregado()){
                colocarMarker(BitmapDescriptorFactory.HUE_GREEN,destinos.get(i));
            }else if(destinos.get(i).getCancelado()){
                colocarMarker(BitmapDescriptorFactory.HUE_RED,destinos.get(i));
            }else if(destinos.get(i).getAgregadoDurRecorrido()) {
                colocarMarker(BitmapDescriptorFactory.HUE_BLUE,destinos.get(i));
            }else{
                switch (destinos.get(i).getId_tipo_registro()){
                    case 1:
                        colocarMarker(BitmapDescriptorFactory.HUE_AZURE,destinos.get(i));
                        break;
                    case 2:
                        colocarMarker(BitmapDescriptorFactory.HUE_CYAN,destinos.get(i));
                        break;
                    default:
                        colocarMarker(BitmapDescriptorFactory.HUE_AZURE,destinos.get(i));
                        break;

                }
            }
        }
        }

    }

    private void colocarMarker(float hue, Destinos destino) {
        Marker marker;
        String title;
        String estado = "";
        if(destino.getNombre_transporte().equals("SUCURSAL CLIENTE")){
            title="Cliente: "+destino.getNombre_cliente()+" --- Dir: "+destino.getDireccion();
        }else {
            title="Transp: "+destino.getNombre_transporte()+" --- Dir: "+destino.getDireccion();
        }
        if (!destino.getEntregado()){
            estado="Pendiente";
        }else if(destino.getEntregado()){
            estado="Entregado";
        }else if(destino.getCancelado()){
            estado="Cancelado";
        }
        marker=mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(hue))
                .title(title)
                .snippet("Pos: "+ String.valueOf(destino.getOrden())+" -- Tipo: "+destino.getMotivo()+" -- Estado: "+estado)
                .position(new LatLng(destino.getLatitude(), destino.getLongitude())));
        marker.setTag(destino.getId());

    }

    public void irAGoogleMaps() {

        Intent i = new Intent(context,QrReader.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,i, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setAutoCancel(true)
                .setContentText("Marcar como entregado")
                .setSmallIcon(R.drawable.qrcode)
                .setContentTitle("ENTREGAR")
                .setOngoing(true)
                .setContentIntent(pendingIntent);
        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        manager.notify(0,builder.build());
        DireccionesMapsApi direccionesMapsApi=new DireccionesMapsApi();
        //ordenarArrayListDestinos();
        direccionesMapsApi.getRequestedUrl(almacenDestinos.getArrayList("arrayDestinosKey"),false);
        Uri uri = Uri.parse(almacenDestinos.getUrlGoogleMaps());
        Intent intent2 = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent2);
        if(context instanceof QrReader){
            ((Activity)context).finish();
        }

        almacenDestinos.setGoogleMapsApp(true);
        almacenDestinos.setComenzoRecorrido(true);

    }
}
