package com.example.ingeniera.trakeoentregas;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class AlmacenDestinos {

    private static String PREFERENCIAS= "Usuario";
    private Context context;


    public AlmacenDestinos (Context context){
        this.context=context;   //al llamar se pasa el contexto de donde de llamo
    }

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


}
