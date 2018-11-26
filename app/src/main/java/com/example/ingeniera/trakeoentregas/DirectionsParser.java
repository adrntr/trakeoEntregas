package com.example.ingeniera.trakeoentregas;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.ingeniera.trakeoentregas.SolicitarDestinos.almacenDestinos;

/**
 * Created by NgocTri on 12/11/2017.
 * Modify by Trillo Adrian on 17/11/2018
 */

/*Explanation
*
*Google maps Api return an JSON Object with:
*   Routes: The whole way you want to do.
*       Legs: Each way between destinations (e.g. Origin-->Waypoint_1-->Waypoint_2-->Destination in this case there are 3 Legs)
*           Steps: Each Legs contain steps, which are decode into "Polylines" (e.g. vajrExqmdJv@|BfAvC~@rC`AnCxB~FBHrAtD)
*               Polylines: After decode it, we obtain a Arraylist<LatLng> with a series of points.
* */

public class DirectionsParser {
    /**
     * Returns a list of lists containing latitude and longitude from a JSONObject
     */
    public List<List<HashMap<String, String>>> parse(JSONObject jObject) {

        List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String, String>>>();
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;
        JSONArray jWaypointsOpt=null;
        Integer waypoint;

        try {

            jRoutes = jObject.getJSONArray("routes");


            // Loop for all routes
            for (int i = 0; i < jRoutes.length(); i++) {
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                jWaypointsOpt = ((JSONObject) jRoutes.get(i)).getJSONArray("waypoint_order");
                ArrayList<Integer> waypointOrder =new ArrayList<>();

                //Loop for all legs
                for (int j = 0; j < jLegs.length(); j++) {
                    jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");
                    List path = new ArrayList<HashMap<String, String>>();
                    int duration = 0;
                    duration = (int) ((JSONObject) ((JSONObject) jLegs.get(j)).get("duration")).get("value");

                    //Loop for all steps
                    for (int k = 0; k < jSteps.length(); k++) {
                        String polyline = "";
                        polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                        List list = decodePolyline(polyline);

                        //Loop for all points
                        for (int l = 0; l < list.size(); l++) {
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("lat", Double.toString(((LatLng) list.get(l)).latitude));
                            hm.put("lon", Double.toString(((LatLng) list.get(l)).longitude));
                            path.add(hm);
                        }
                    }

                    routes.add(path); //Add all the points for one of the Legs
                }
                for(int n=0;n<jWaypointsOpt.length();n++){
                    waypoint= jWaypointsOpt.getInt(n);
                    waypointOrder.add(waypoint);
                }
                almacenDestinos.setArrayWaypointOrder(waypointOrder);
                ordenarArrayListDestinos();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
        }

        return routes;//return all the legs
    }

    /**
     * Method to decode polyline
     * Source : http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
     */
    private List decodePolyline(String encoded) {

        List poly = new ArrayList();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    private void ordenarArrayListDestinos() {

        ArrayList<Integer> waypointsOrder = almacenDestinos.getArrayWaypointOrder("waypointsOrderKey");
        ArrayList<Destinos> destinos=almacenDestinos.getArrayList("arrayDestinosKey");

        ArrayList<Destinos> tempDestino=new ArrayList<>();
        for (int j=0;j<destinos.size();j++){
            tempDestino.add(new Destinos());
        }


        for (int i=0;i<waypointsOrder.size();i++){
            int ubicacion=waypointsOrder.get(i);
            tempDestino.set(i,destinos.get(ubicacion));
        }
        almacenDestinos.saveArrayList(tempDestino);
    }

}