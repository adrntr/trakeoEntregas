package com.example.ingeniera.trakeoentregas;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String REQUESTING_LOCATION_UPDATES_KEY = "RLUK" ;
    private GoogleMap mMap;
    private static final int LOCATION_REQUEST = 500;
    ArrayList<LatLng> listPoints;

    private FusedLocationProviderClient mFusedLocationClient; //Necessary to obtain client's location
    private Location mCurrentLocation;
    private LocationCallback mLocationCallback;
    boolean mRequestingLocationUpdates=true;

    ConvertirLatLng convertirLatLng;
    RealTimeLocation realTimeLocation;

    private ArrayList<Destinos> destinos;

    public static AlmacenDestinos almacenDestinos; //defino

    private String codigo="id_planilla";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        almacenDestinos=new AlmacenDestinos(this);

        //get whether the app have to get user's location or not
        updateValuesFromBundle(savedInstanceState);
        listPoints = new ArrayList<>();

        convertirLatLng=new ConvertirLatLng(MapsActivity.this);
        realTimeLocation=new RealTimeLocation(MapsActivity.this);
        //SETTINGS to get user's location
        realTimeLocation.userLocationSettings();

        //Get latitude and longitude based in the settings from mLocationRequest
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(MapsActivity.this);

        destinos=new ArrayList<>();

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST);
            return;
        }
        mMap.setMyLocationEnabled(true);

        //obtain last user's location
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            Toast.makeText(getApplicationContext(),"Lat: "+Double.toString(location.getLatitude())+"\nLon: "
                                    +Double.toString(location.getLongitude()),Toast.LENGTH_SHORT).show();

                            mCurrentLocation=new Location(location);

                            LatLng currentLatLng = new LatLng(location.getLatitude(),location.getLongitude());

                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng,12));

                            convertirLatLng.startIntentService(mCurrentLocation); //convierto la ubicacion en una dirección

                        }
                    }
                });


        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                //reset marker when already 2
                if(listPoints.size()==2){
                    listPoints.clear();
                    mMap.clear();
                }
                listPoints.add(latLng);
                //create maker
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                if(listPoints.size()==1){
                    //add first marker to the map
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                }else{
                    //add second marker to the map
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }
                mMap.addMarker(markerOptions);

                if(listPoints.size()==2){
                    //create the URL to get request from first marker to second marker
                    DireccionesMapsApi direccionesMapsApi=new DireccionesMapsApi();
                    String url = direccionesMapsApi.getRequestedUrl(listPoints.get(0),listPoints.get(1),mMap);
                    TaskRequestDirections taskRequestDirections=new TaskRequestDirections();
                    taskRequestDirections.execute(url);

                }
            }
        });

        obtenerDatos("409");

        MarkerOptions markerOptions2 = new MarkerOptions();
        markerOptions2.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        destinos=almacenDestinos.getArrayList("arrayDestinosKey");
        if(destinos!=null){
            for (int j=0;j<destinos.size();j++){
                LatLng latLng2= new LatLng(destinos.get(j).getLatitude(),destinos.get(j).getLongitude());
                markerOptions2.position(latLng2);
                mMap.addMarker(markerOptions2);
            }
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

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case LOCATION_REQUEST:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    mMap.setMyLocationEnabled(true);

                    //obtain last user's location
                    mFusedLocationClient.getLastLocation()
                            .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    // Got last known location. In some rare situations this can be null.
                                    if (location != null) {
                                        Toast.makeText(getApplicationContext(),"Lat: "+Double.toString(location.getLatitude())+"\nLon: "
                                                +Double.toString(location.getLongitude()),Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                break;
        }
    }

    public class TaskRequestDirections extends AsyncTask<String,Void,String>{

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

            LatLng latLngAnt;
            ArrayList points = null;
            int index=0;
            for(List<HashMap<String,String>> path : lists ){
                index++;
                points=new ArrayList();
                for(HashMap<String,String> point : path){
                    double lat=Double.parseDouble(point.get("lat"));
                    double lon=Double.parseDouble(point.get("lon"));

                    points.add(new LatLng(lat,lon));
                }


                if(points!=null){
                    if(index%2==0){
                        mMap.addPolyline(new PolylineOptions()
                                .color(Color.BLUE)
                                .width(10)
                                .addAll(points));
                    }else {
                        mMap.addPolyline(new PolylineOptions()
                                .color(Color.RED)
                                .width(10)
                                .addAll(points));
                    }

                }else {
                    Toast.makeText(getApplicationContext(),"Direction not found",Toast.LENGTH_SHORT).show();
                }






            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mRequestingLocationUpdates) {
            realTimeLocation.startLocationUpdates(mFusedLocationClient);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        realTimeLocation.stopLocationUpdates(mFusedLocationClient);
    }
    //whether change of activity it funtion save some states
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY,mRequestingLocationUpdates);
        super.onSaveInstanceState(outState);
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }
        // Update the value of mRequestingLocationUpdates from the Bundle.
        if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
            mRequestingLocationUpdates = savedInstanceState.getBoolean(REQUESTING_LOCATION_UPDATES_KEY);
        }

        // ...

        // Update UI to match restored state
        //updateUI();
    }


    private void obtenerDatos(final String numPedidos) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://sistemas.andif.com.ar/pruebas/prueba-remito-transporte/datos-planilla-seguro.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("registros");
                    Destinos destino;
                    ArrayList<Destinos> destinos = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        destino = new Destinos();
                        JSONObject jsonObjectExplorer = jsonArray.getJSONObject(i);
                        destino.setIdCliente(jsonObjectExplorer.optInt("id_cliente"));
                        destino.setCantidadBultos(jsonObjectExplorer.optInt("cantidad_bultos"));
                        destino.setNombre_cliente(jsonObjectExplorer.optString("nombre_cliente"));
                        destino.setTransporte(jsonObjectExplorer.optString("transporte"));
                        destino.setDireccion_transporte(jsonObjectExplorer.optString("direccion_transporte"));
                        destino.setLatitude(jsonObjectExplorer.optDouble("latitud"));
                        destino.setLongitude(jsonObjectExplorer.optDouble("longitud"));
                        destinos.add(destino);
                    }

                    almacenDestinos.saveArrayList(destinos);
                } catch (JSONException e) {
                    e.printStackTrace();
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
                params.put(codigo, numPedidos);
                return params;
            }
        };


        queue.add(stringRequest);

    }



}

