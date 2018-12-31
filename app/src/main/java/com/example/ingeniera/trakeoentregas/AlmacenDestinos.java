package com.example.ingeniera.trakeoentregas;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.ingeniera.trakeoentregas.Destino.Destinos;
import com.example.ingeniera.trakeoentregas.Destino.Usuarios;
import com.example.ingeniera.trakeoentregas.Ingreso.HojasDeRuta;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AlmacenDestinos {

    private static String PREFERENCIAS= "Usuario";
    private Context context;


    public AlmacenDestinos (Context context){
        this.context=context;   //al llamar se pasa el contexto de donde de llamo
    }


    /***********/

    public void saveArrayList(ArrayList<Destinos> list){
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString("arrayDestinosKey", json);
        editor.apply();     // This line is IMPORTANT !!!
    }

    public ArrayList<Destinos> getArrayList(String key){
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<Destinos>>() {}.getType();
        return gson.fromJson(json, type);
    }

    /***********/

    public void saveArrayDestinosFiltrados(ArrayList<Destinos> list){
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString("arrayDestinosFiltradosKey", json);
        editor.apply();     // This line is IMPORTANT !!!
    }

    public ArrayList<Destinos> getArrayDestinosFiltrados(){
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("arrayDestinosFiltradosKey", null);
        Type type = new TypeToken<ArrayList<Destinos>>() {}.getType();
        return gson.fromJson(json, type);
    }
    /***********/
    public void saveArrayDestinosBackUp(ArrayList<Destinos> list){
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString("arrayDestinosKey", json);
        editor.apply();     // This line is IMPORTANT !!!
    }

    public ArrayList<Destinos> getArrayDestinosBackUp(String key){
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<Destinos>>() {}.getType();
        return gson.fromJson(json, type);
    }

    /***********/

    public String getLat (){
        SharedPreferences preferencias = context.getSharedPreferences(PREFERENCIAS,Context.MODE_PRIVATE);
        String s = preferencias.getString("lat",""); //me devuelve el string, si no lo encuentra ""
        return s;
    }

    public String getLng (){
        SharedPreferences preferencias = context.getSharedPreferences(PREFERENCIAS,Context.MODE_PRIVATE);
        String s = preferencias.getString("lng",""); //me devuelve el string, si no lo encuentra ""
        return s;
    }

    public void setCurrentPosition(Double lat,Double lng){
        SharedPreferences preferencias = context.getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE); //Se crea un archivo de nombre PREFERENCIAS para guardar las puntuaciones
        SharedPreferences.Editor editor = preferencias.edit(); //crea un editor para modificar el archivo
        editor.putString("lat",lat.toString()); //le paso una key y el valor (string)
        editor.putString("lng",lng.toString()); //le paso una key y el valor (string)
        editor.commit();//aplico los cambios
    }

    /***********/

    public String getCurrentAddress (){
        SharedPreferences preferencias = context.getSharedPreferences(PREFERENCIAS,Context.MODE_PRIVATE);
        String s = preferencias.getString("currentAddress",""); //me devuelve el string, si no lo encuentra ""
        return s;
    }


    public void setCurrentAddress(String currentAddress){
        SharedPreferences preferencias = context.getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE); //Se crea un archivo de nombre PREFERENCIAS para guardar las puntuaciones
        SharedPreferences.Editor editor = preferencias.edit(); //crea un editor para modificar el archivo
        editor.putString("currentAddress",currentAddress); //le paso una key y el valor (string)
        editor.commit();//aplico los cambios
    }

    /***********/

    public int getEstadoRuta (){
        SharedPreferences preferencias = context.getSharedPreferences(PREFERENCIAS,Context.MODE_PRIVATE);
        int estadoRuta = preferencias.getInt("EstadoRuta",0); //me devuelve el string, si no lo encuentra ""
        return estadoRuta;
    }

    public void setEstadoRuta(int estadoRuta){
        SharedPreferences preferencias = context.getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE); //Se crea un archivo de nombre PREFERENCIAS para guardar las puntuaciones
        SharedPreferences.Editor editor = preferencias.edit(); //crea un editor para modificar el archivo
        editor.putInt("EstadoRuta",estadoRuta);     //0 --> ruta no iniciada
                                                    //1 --> Hojas de ruta obtenidas pero no se selecciono alguna
                                                    //2 --> Se selecciono una hoja de ruta y se obtuvieron los destinos
                                                    //3 --> direcciones obtenidas
                                                    //4 --> Hoja de ruta seleccionada pero no se presiono ir a todos
        editor.commit();//aplico los cambios
    }

    /***********/

    public void saveArrayListPuntos(List<List<HashMap<String, String>>> lists){
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(lists);
        editor.putString("arrayPuntosKey", json);
        editor.apply();     // This line is IMPORTANT !!!
    }

    public List<List<HashMap<String, String>>> getArrayListPuntos(String key){
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<List<List<HashMap<String,String>>>>() {}.getType();
        return gson.fromJson(json, type);
    }

    /***********/

    public String getUrlGoogleMaps (){
        SharedPreferences preferencias = context.getSharedPreferences(PREFERENCIAS,Context.MODE_PRIVATE);
        String urlGoogleMaps = preferencias.getString("urlGoogleMaps",""); //me devuelve el string, si no lo encuentra ""
        return urlGoogleMaps;
    }

    public void setUrlGoogleMaps(String urlGoogleMaps){
        SharedPreferences preferencias = context.getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE); //Se crea un archivo de nombre PREFERENCIAS para guardar las puntuaciones
        SharedPreferences.Editor editor = preferencias.edit(); //crea un editor para modificar el archivo
        editor.putString("urlGoogleMaps",urlGoogleMaps);     //0 --> ruta no iniciada
        editor.commit();//aplico los cambios
    }

    /***********/

    public void setArrayWaypointOrder(ArrayList<Integer> list){
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString("waypointsOrderKey", json);
        editor.apply();     // This line is IMPORTANT !!!
    }

    public ArrayList<Integer> getArrayWaypointOrder(String key){
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<Integer>>() {}.getType();
        return gson.fromJson(json, type);
    }


    public void setArrayHojasDeRutas(ArrayList<HojasDeRuta> list){
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString("arrayHojasDeRutaKey", json);
        editor.apply();     // This line is IMPORTANT !!!
    }

    public ArrayList<HojasDeRuta> getArrayHojasDeRutas(String key){
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<HojasDeRuta>>() {}.getType();
        return gson.fromJson(json, type);
    }

    /***********/



    public void setToken(String token) {

        SharedPreferences preferencias = context.getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE); //Se crea un archivo de nombre PREFERENCIAS para guardar las puntuaciones
        SharedPreferences.Editor editor = preferencias.edit(); //crea un editor para modificar el archivo
        editor.putString("tokenKey",token); //le paso una key y el valor (string)
        editor.commit();//aplico los cambios

    }

    public String getToken (){
        SharedPreferences preferencias = context.getSharedPreferences(PREFERENCIAS,Context.MODE_PRIVATE);
        String token = preferencias.getString("tokenKey",""); //me devuelve el string, si no lo encuentra ""
        return token;
    }


/*
    public void setUsuario(String dni,String nombreApellido) {

        SharedPreferences preferencias = context.getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE); //Se crea un archivo de nombre PREFERENCIAS para guardar las puntuaciones
        SharedPreferences.Editor editor = preferencias.edit(); //crea un editor para modificar el archivo
        editor.putString("dniKey",dni);
        editor.putString("nombreApellidoKey",nombreApellido);
        editor.commit();//aplico los cambios

    }


    public String getUsuario (String key){
        SharedPreferences preferencias = context.getSharedPreferences(PREFERENCIAS,Context.MODE_PRIVATE);
        String s = preferencias.getString(key,""); //me devuelve el string, si no lo encuentra ""
        return s;
    }

    /***********/

    public void setArrayUsuarios(ArrayList<Usuarios> list){
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString("arrayUsuariosKey", json);
        editor.apply();     // This line is IMPORTANT !!!
    }

    public ArrayList<Usuarios> getArrayUsuarios(String key){
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<Usuarios>>() {}.getType();
        return gson.fromJson(json, type);
    }

    /***********/

    public void setGoogleMapsApp(Boolean googleMapsApp) {

        SharedPreferences preferencias = context.getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE); //Se crea un archivo de nombre PREFERENCIAS para guardar las puntuaciones
        SharedPreferences.Editor editor = preferencias.edit(); //crea un editor para modificar el archivo
        editor.putBoolean("googleMpasAppKey",googleMapsApp);
        editor.commit();//aplico los cambios

    }


    public Boolean getGoogleMapsApp (String key){
        SharedPreferences preferencias = context.getSharedPreferences(PREFERENCIAS,Context.MODE_PRIVATE);
        Boolean googleMpasApp = preferencias.getBoolean(key,false); //me devuelve el string, si no lo encuentra ""
        return googleMpasApp;
    }

    /***********/
    public void setComenzoRecorrido(Boolean comenzoRecorrido) {

        SharedPreferences preferencias = context.getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE); //Se crea un archivo de nombre PREFERENCIAS para guardar las puntuaciones
        SharedPreferences.Editor editor = preferencias.edit(); //crea un editor para modificar el archivo
        editor.putBoolean("comenzoRecorridoKey",comenzoRecorrido);
        editor.commit();//aplico los cambios

    }


    public Boolean getComenzoRecorrido (){
        SharedPreferences preferencias = context.getSharedPreferences(PREFERENCIAS,Context.MODE_PRIVATE);
        Boolean comenzoRecorrido = preferencias.getBoolean("comenzoRecorridoKey",false); //me devuelve el string, si no lo encuentra false
        return comenzoRecorrido;
    }

    /***********/



    public String getIdHojaDeRuta (){
        SharedPreferences preferencias = context.getSharedPreferences(PREFERENCIAS,Context.MODE_PRIVATE);
        String idHojaDeRuta = preferencias.getString("idHojaDeRutaKey",""); //me devuelve el string, si no lo encuentra ""
        return idHojaDeRuta;
    }

    public void setIdHojaDeRuta(String idHojaDeRuta){
        SharedPreferences preferencias = context.getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE); //Se crea un archivo de nombre PREFERENCIAS para guardar las puntuaciones
        SharedPreferences.Editor editor = preferencias.edit(); //crea un editor para modificar el archivo
        editor.putString("idHojaDeRutaKey", idHojaDeRuta);
        editor.commit();//aplico los cambios
    }


    /***********/

    public void setContextLista(Context contextLista){
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(contextLista);
        editor.putString("contextLista", json);
        editor.apply();     // This line is IMPORTANT !!!
    }

    public Context getContextLista(){
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("contextLista", null);
        Type type = new TypeToken<Context>() {}.getType();
        return gson.fromJson(json, type);
    }
    /***********/

    public void setFiltros(Boolean pendiente,Boolean cumplido,Boolean cancelado,Boolean agregado) {

        SharedPreferences preferencias = context.getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE); //Se crea un archivo de nombre PREFERENCIAS para guardar las puntuaciones
        SharedPreferences.Editor editor = preferencias.edit(); //crea un editor para modificar el archivo
        editor.putBoolean("pendienteKey",pendiente);
        editor.putBoolean("cumplidoKey",cumplido);
        editor.putBoolean("canceladoKey",cancelado);
        editor.putBoolean("agregadoKey",agregado);
        editor.commit();//aplico los cambios

    }


    public Boolean getFiltros (String key){
        SharedPreferences preferencias = context.getSharedPreferences(PREFERENCIAS,Context.MODE_PRIVATE);
        Boolean filtro = preferencias.getBoolean(key,true); //me devuelve el string, si no lo encuentra ""
        return filtro;
    }

}
