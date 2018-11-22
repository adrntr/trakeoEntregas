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

    public void getRequestedUrl(ArrayList<Destinos> destinos) {
        ArrayList<LatLng> waypoints =new ArrayList<>();

        for(int i=0;i<destinos.size();i++){
            if(destinos.get(i).getLatitude()!=0&&destinos.get(i).getLongitude()!=0) {
                waypoints.add(new LatLng(destinos.get(i).getLatitude(), destinos.get(i).getLongitude()));
            }
        }
        String waypointsStr="waypoints=";
        Boolean inicio=true;
        for (LatLng paradas : waypoints){

            if(inicio){
                waypointsStr+=paradas.latitude+","+paradas.longitude;
                inicio=false;
            }else {
                waypointsStr+="|"+paradas.latitude+","+paradas.longitude;
            }
        }
        //value of origin =
        String str_org = "origin="+almacenDestinos.getLat()+","+almacenDestinos.getLng();
        //value of detination
        String str_dest = "destination="+almacenDestinos.getLat()+","+almacenDestinos.getLng();
        //set value enable the sensor
        String sensor="sensor=false";
        //mode for find direction
        String mode= "mode=driving";
        //Waypoints
        //String wayPoints= "waypoints=-34.6353325,-58.3690203|CALIFORNIA3099,BARRACAS|Brandsen4755,Ciudadela";
        //build the full param
        String param = str_org+"&"+str_dest+"&"+sensor+"&"+mode+"&"+waypointsStr+"&key=AIzaSyBh8thmOqQy78-ozgmQOYIdKgqHDCKgDME";
        String output = "json";
        //create url to request
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+param;

        TaskRequestDirections taskRequestDirections=new TaskRequestDirections();
        taskRequestDirections.execute(url);


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

    public void agregarMarkers() {

        ArrayList<Destinos> destinos = almacenDestinos.getArrayList("arrayDestinosKey");
        for (int i = 0; i < destinos.size(); i++) {
            MarkerOptions markerOptions2 = new MarkerOptions();
            markerOptions2.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            LatLng latLng2 = new LatLng(destinos.get(i).getLatitude(), destinos.get(i).getLongitude());
            markerOptions2.position(latLng2);
            mMap.addMarker(markerOptions2);
        }

    }
}
