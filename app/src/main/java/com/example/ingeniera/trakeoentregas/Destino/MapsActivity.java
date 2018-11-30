package com.example.ingeniera.trakeoentregas.Destino;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.example.ingeniera.trakeoentregas.ConvertirLatLng;
import com.example.ingeniera.trakeoentregas.Entregas.QrReader;
import com.example.ingeniera.trakeoentregas.Ingreso.SolicitarDestinos;
import com.example.ingeniera.trakeoentregas.R;
import com.example.ingeniera.trakeoentregas.RealTimeLocation;
import com.example.ingeniera.trakeoentregas.TransporteInfo;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

import static com.example.ingeniera.trakeoentregas.Ingreso.SolicitarDestinos.almacenDestinos;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,GoogleMap.OnMapClickListener,GoogleMap.OnMarkerClickListener,GoogleMap.OnInfoWindowClickListener {

    public static final String KEY_EXTRA = "numeroPedido" ;
    private static final String REQUESTING_LOCATION_UPDATES_KEY = "RLUK" ;
    private static final int QR_REQUEST = 600 ;
    private GoogleMap mMap;
    private static final int LOCATION_REQUEST = 500;
    ArrayList<LatLng> listPoints;

    private FusedLocationProviderClient mFusedLocationClient; //Necessary to obtain client's location
    private Location mCurrentLocation;
    private LocationCallback mLocationCallback;
    boolean mRequestingLocationUpdates=true;

    ConvertirLatLng convertirLatLng;
    RealTimeLocation realTimeLocation;

    /**
     * Keeps track of the selected marker.
     */
    private Marker mSelectedMarker;

    Button irATodosMapsBt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //get whether the app have to get user's location or not
        updateValuesFromBundle(savedInstanceState);
        listPoints = new ArrayList<>();

        convertirLatLng=new ConvertirLatLng(MapsActivity.this);
        realTimeLocation=new RealTimeLocation(MapsActivity.this);
        //SETTINGS to get user's location
        realTimeLocation.userLocationSettings();

        //Get latitude and longitude based in the settings from mLocationRequest
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(MapsActivity.this);

        irATodosMapsBt=findViewById(R.id.irATodosMapsBt);

        irATodosMapsBt.setOnClickListener(clicListener);

        setTitle("MAPA - "+ almacenDestinos.getUsuario("nombreApellidoKey"));

        almacenDestinos.setGoogleMapsApp(false);



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
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            return;
        }
        mMap.setMyLocationEnabled(true);

        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMarkerClickListener(this);

        //obtain last user's location
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            mCurrentLocation = new Location(location);
                            LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 11));
                            convertirLatLng.startIntentService(mCurrentLocation); //convierto la ubicacion en una dirección
                        }
                    }
                });

        /*Consulto el estado de la aplicación para evitar pedir las direcciones de forma repetida
         * */

        DireccionesMapsApi direccionesMapsApi = new DireccionesMapsApi(mMap, MapsActivity.this);
        switch (almacenDestinos.getEstadoRuta()) {
            case 0:
                finish();
                break;
            case 1:
                finish();
                break;
            case 2:
                ArrayList<Destinos> destinos = almacenDestinos.getArrayList("arrayDestinosKey");
                direccionesMapsApi.agregarMarkers();
                direccionesMapsApi.getRequestedUrl(destinos,true);
                break;
            case 3:
                direccionesMapsApi.agregarMarkers();
                direccionesMapsApi.agregarLineas();
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mapsactivity_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();

        switch (id){

            case R.id.Terminar:
                almacenDestinos.setEstadoRuta(0);
                Intent intent=new Intent(MapsActivity.this,SolicitarDestinos.class);
                startActivity(intent);
                finish();
                break;
            case R.id.LeerQR:
                Intent intent1=new Intent(MapsActivity.this,QrReader.class);
                startActivityForResult(intent1,QR_REQUEST);
                break;
            case R.id.MapsApp:
                DireccionesMapsApi direccionesMapsApi=new DireccionesMapsApi(mMap,MapsActivity.this);
                //ordenarArrayListDestinos();
                direccionesMapsApi.getRequestedUrl(almacenDestinos.getArrayList("arrayDestinosKey"),false);
                Uri uri = Uri.parse(almacenDestinos.getUrlGoogleMaps());
                Intent intent2 = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent2);
                finish();
                break;
            case R.id.listDestinos:
                Intent intent4=new Intent(MapsActivity.this,ListaDestinos.class);
                startActivity(intent4);
                finish();
        }




        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case QR_REQUEST:
                Intent intent=new Intent(MapsActivity.this,MapsActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case LOCATION_REQUEST:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(MapsActivity.this, "Permiso aceptado", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(MapsActivity.this, MapsActivity.class);
                        startActivity(i);
                        finish();
                    } else {

                        Toast.makeText(MapsActivity.this, "Permiso NO aceptado", Toast.LENGTH_SHORT).show();
                    }
                    return;
                }


        }
    }


    View.OnClickListener clicListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch(v.getId()){
                case R.id.irATodosMapsBt:
                    DireccionesMapsApi direccionesMapsApi=new DireccionesMapsApi(MapsActivity.this);
                    direccionesMapsApi.irAGoogleMaps();
                    break;

            }
        }
    };





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

    @Override
    protected void onRestart() {
        super.onRestart();
        if (!almacenDestinos.getGoogleMapsApp("googleMpasAppKey")) {
            Intent intent = new Intent(MapsActivity.this, MapsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            finish();
        }
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

    @Override
    public void onMapClick(LatLng latLng) {
        // Any showing info window closes when the map is clicked.
        // Clear the currently selected marker.
        mSelectedMarker = null;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        // The user has re-tapped on the marker which was already showing an info window.
        if (marker.equals(mSelectedMarker)) {
            // The showing info window has already been closed - that's the first thing to happen
            // when any marker is clicked.
            // Return true to indicate we have consumed the event and that we do not want the
            // the default behavior to occur (which is for the camera to move such that the
            // marker is centered and for the marker's info window to open, if it has one).
            mSelectedMarker = null;
            return true;
        }

        mSelectedMarker = marker;

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur.
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        int idDestino= (int) marker.getTag();
        Toast.makeText(this,"TAG= "+idDestino,Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(this,TransporteInfo.class);
        intent.putExtra("idDestino",idDestino);
        startActivity(intent);

    }
}

