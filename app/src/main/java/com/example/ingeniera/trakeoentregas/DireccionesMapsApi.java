package com.example.ingeniera.trakeoentregas;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Map;

public class DireccionesMapsApi {

    public String getRequestedUrl(LatLng origin, LatLng dest,GoogleMap mMap) {
        ArrayList<LatLng> waypoints =new ArrayList<>();
        waypoints.add(new LatLng(-34.673582, -58.574977));
        waypoints.add(new LatLng(-34.672977, -58.574432));
        waypoints.add(new LatLng(-34.681377,-58.573032));
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
}
