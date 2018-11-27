package com.example.ingeniera.trakeoentregas;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.Toast;

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
import java.util.Map;

import static com.example.ingeniera.trakeoentregas.SolicitarDestinos.almacenDestinos;

public class DireccionesMapsApi {

    GoogleMap mMap;
    Context context;

    public DireccionesMapsApi(GoogleMap mMap, Context context) {
        this.mMap=mMap;
        this.context=context;
    }

    public void getRequestedUrl(ArrayList<Destinos> destinos,Boolean pedirDestinos) {
        ArrayList<LatLng> waypoints =new ArrayList<>();

        for(int i=0;i<destinos.size();i++){
            if(destinos.get(i).getLatitude()!=0&&destinos.get(i).getLongitude()!=0) {
                waypoints.add(new LatLng(destinos.get(i).getLatitude(), destinos.get(i).getLongitude()));
            }
        }
        String waypointsStr="",str_org = null,str_dest=null;
        if(pedirDestinos){
            for (int i=0;i<waypoints.size();i++){
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
                if (!(destinos.get(i).getEntregado())){
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
        str_dest = "destination="+almacenDestinos.getLat()+","+almacenDestinos.getLng();
        String sensor="sensor=false";
        //mode for find direction
        String mode= "mode=driving";
        //Waypoints
        String param = str_org+"&"+str_dest+"&"+sensor+"&"+mode+"&"+"waypoints=optimize:true|"+waypointsStr;
        String paramMaps=str_org+"&"+str_dest+"&"+sensor+"&"+mode+"&"+"waypoints="+waypointsStr;
        String output = "json";
        //create url to request
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+param+"&key=AIzaSyBh8thmOqQy78-ozgmQOYIdKgqHDCKgDME";
        String urlGoogleMaps="https://www.google.com/maps/dir/?api=1&"+paramMaps;
        almacenDestinos.setUrlGoogleMaps(urlGoogleMaps);
        if(pedirDestinos){
            TaskRequestDirections taskRequestDirections=new TaskRequestDirections();
            taskRequestDirections.execute(url);
        }



    }

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
            almacenDestinos.setEstadoRuta(2);

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
            getRequestedUrl(almacenDestinos.getArrayList("arrayDestinosKey"),true);
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
                    Toast.makeText(context, "Direction not found", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    public void agregarMarkers() {

        ArrayList<Destinos> destinos = almacenDestinos.getArrayList("arrayDestinosKey");
        if (destinos!=null&&mMap!=null){

        for (int i = 0; i < destinos.size(); i++) {

            if (destinos.get(i).getEntregado()){
                colocarMarker(BitmapDescriptorFactory.HUE_RED,destinos.get(i));
            }else{
                switch (destinos.get(i).getTipoEntrega()){
                    case 1:
                        colocarMarker(BitmapDescriptorFactory.HUE_BLUE,destinos.get(i));
                        break;
                    case 2:
                        colocarMarker(BitmapDescriptorFactory.HUE_ORANGE,destinos.get(i));
                        break;
                    case 3:
                        colocarMarker(BitmapDescriptorFactory.HUE_GREEN,destinos.get(i));
                        break;
                    case 4:
                        colocarMarker(BitmapDescriptorFactory.HUE_MAGENTA,destinos.get(i));
                        break;
                        default:
                            colocarMarker(BitmapDescriptorFactory.HUE_GREEN,destinos.get(i));
                            break;


                }
            }
        }
        }

    }

    private void colocarMarker(float hue, Destinos destino) {
        Marker marker;
        marker=mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(hue))
                .title(destino.getTransporte())
                .snippet(String.valueOf(destino.getIdCliente()))
                .position(new LatLng(destino.getLatitude(), destino.getLongitude())));
        marker.setTag(destino.getIdCliente());

    }
}
