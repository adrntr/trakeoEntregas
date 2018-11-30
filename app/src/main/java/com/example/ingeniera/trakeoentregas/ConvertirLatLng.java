package com.example.ingeniera.trakeoentregas;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;

public class ConvertirLatLng {
    private Context context;
    private AddressResultReceiver mResultReceiver;
    private Location mLastKnownLocation;
    private FusedLocationProviderClient mFusedLocationClient;

    public ConvertirLatLng(Context context) {
        this.context=context;
    }

    public void startIntentService(Location mLastKnownLocation) {
        Intent intent = new Intent(context, FetchAddressIntentService.class);
        intent.putExtra(FetchAddressIntentService.Constants.RECEIVER, mResultReceiver= new AddressResultReceiver(new Handler()));
        intent.putExtra(FetchAddressIntentService.Constants.LOCATION_DATA_EXTRA, mLastKnownLocation);
        context.startService(intent);
    }

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if (resultData == null) {
                return;
            }

            // Display the address string
            // or an error message sent from the intent service.
            String mAddressOutput = resultData.getString(FetchAddressIntentService.Constants.RESULT_DATA_KEY);
            if (mAddressOutput == null) {
                mAddressOutput = "";
            }
            //Toast.makeText(context,mAddressOutput,Toast.LENGTH_SHORT).show();

            // Show a toast message if an address was found.
            if (resultCode == FetchAddressIntentService.Constants.SUCCESS_RESULT) {
                //Toast.makeText(context,"Address encontrada",Toast.LENGTH_SHORT).show();
            }

        }
    }
}
