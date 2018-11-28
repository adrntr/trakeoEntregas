package com.example.ingeniera.trakeoentregas;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.location.Location;
import android.support.annotation.NonNull;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import static com.example.ingeniera.trakeoentregas.Ingreso.SolicitarDestinos.almacenDestinos;

public class RealTimeLocation {

    Context context;
    private LocationRequest mLocationRequest;//object that contain settings for getting user's location
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CHECK_SETTINGS =400 ;



    public RealTimeLocation(Context context) {
        this.context=context;

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }
            for (Location location : locationResult.getLocations()) {

                Double latitude=location.getLatitude();
                Double longitude=location.getLongitude();

                almacenDestinos.setCurrentPosition(latitude,longitude);

            }
        };
    };



    public void userLocationSettings() {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(60000);// This method sets the rate in milliseconds at which your app prefers to receive location updates
        mLocationRequest.setFastestInterval(5000);//This method sets the fastest rate in milliseconds at which your app can handle location updates.
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);//This method sets the priority of the request, which gives the Google Play services location services a strong hint about which location sources to use
        //Get the location settings
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        //LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();

        SettingsClient client = LocationServices.getSettingsClient(context);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener((Activity) context, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {

            }
        });

        task.addOnFailureListener((Activity) context, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult((Activity) context,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });


    }


    @SuppressLint("MissingPermission")
    public void startLocationUpdates(FusedLocationProviderClient fusedLocationProviderClient) {
        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null /* Looper */);
    }

    public void stopLocationUpdates(FusedLocationProviderClient fusedLocationProviderClient) {
        fusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
    }


}
