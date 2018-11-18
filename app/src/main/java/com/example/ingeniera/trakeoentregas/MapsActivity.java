package com.example.ingeniera.trakeoentregas;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int REQUEST_CHECK_SETTINGS =400 ;
    private GoogleMap mMap;
    private static final int LOCATION_REQUEST = 500;
    ArrayList<LatLng> listPoints;

    private FusedLocationProviderClient mFusedLocationClient; //Necessary to obtain client's location
    private Location mCurrentLocation;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;//object that contain settings for getting user's location
    boolean mRequestingLocationUpdates=true;

    private static final LatLng ADELAIDE = new LatLng(-34.92873, 138.59995);
    private static final LatLng DARWIN = new LatLng(-12.4258647, 130.7932231);
    private static final LatLng MELBOURNE = new LatLng(-37.81319, 144.96298);
    private static final LatLng PERTH = new LatLng(-31.95285, 115.85734);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        listPoints = new ArrayList<>();

        //SETTINGS to get user's location
        userLocation();

        //Get latitude and longitude based in the settings from mLocationRequest
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(MapsActivity.this);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {

                    Double latitude=location.getLatitude();
                    Double longitude=location.getLongitude();
                }
            };
        };
    }

    private void userLocation() {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(60000);// This method sets the rate in milliseconds at which your app prefers to receive location updates
        mLocationRequest.setFastestInterval(5000);//This method sets the fastest rate in milliseconds at which your app can handle location updates.
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);//This method sets the priority of the request, which gives the Google Play services location services a strong hint about which location sources to use
        //Get the location settings
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        //LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();


        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {

            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MapsActivity.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });


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
                    String url = getRequestedUrl(listPoints.get(0),listPoints.get(1));
                    TaskRequestDirections taskRequestDirections=new TaskRequestDirections();
                    taskRequestDirections.execute(url);

                }
            }
        });
    }

    private String getRequestedUrl(LatLng origin, LatLng dest) {
        ArrayList<LatLng> waypoints =new ArrayList<>();
        waypoints.add(new LatLng(-34.673582, -58.574977));
        waypoints.add(new LatLng(-34.672977, -58.574432));
        waypoints.add(new LatLng(-34.681377,-58.573032));
        //waypoints.add(new LatLng(-34.670177,-58.571532));
        String waypointsStr="waypoints=";
        Boolean inicio=true;
        for (LatLng paradas : waypoints){
            MarkerOptions markerOptionsWayPoint =new MarkerOptions();
            markerOptionsWayPoint.position(paradas);
            markerOptionsWayPoint.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            mMap.addMarker(markerOptionsWayPoint);

            if(inicio){
                waypointsStr+=paradas.latitude+","+paradas.longitude;
                inicio=false;
            }else {
                waypointsStr+="|"+paradas.latitude+","+paradas.longitude;
            }
        }
        //value of origin =
        String str_org = "origin="+origin.latitude+","+origin.longitude;
        //value of detination
        String str_dest = "destination="+dest.latitude+","+dest.longitude;
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
        return url;

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
            startLocationUpdates();
        }
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback,
                    null /* Looper */);


    }
}

