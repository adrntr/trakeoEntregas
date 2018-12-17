package com.example.ingeniera.trakeoentregas.Notificaciones;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.ingeniera.trakeoentregas.Destino.ListaDestinos;
import com.example.ingeniera.trakeoentregas.Destino.TaskObtenerDatosRuta;
import com.example.ingeniera.trakeoentregas.Ingreso.SolicitarDestinos;
import com.example.ingeniera.trakeoentregas.R;
import com.google.firebase.messaging.RemoteMessage;

import static com.example.ingeniera.trakeoentregas.Ingreso.SolicitarDestinos.almacenDestinos;


public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {



    @Override
    public void onCreate() {

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        showNotification(remoteMessage.getData().get("message"));
        Log.i("FirebaseMessagin",remoteMessage.getData().get("message"));

        TaskObtenerDatosRuta taskObtenerDatosRuta = new TaskObtenerDatosRuta(this);
        taskObtenerDatosRuta.execute(almacenDestinos.getIdHojaDeRuta());



    }

    private void showNotification(String message) {

        Intent i = new Intent(getBaseContext(),SolicitarDestinos.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(),0,i, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentText(message)
                .setSmallIcon(R.drawable.acompanante)
                //.setDefaults(Notification.DEFAULT_ALL)
                .setVibrate(new long[] { 100, 1500, 300, 1000, 100, 700, 100 })
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentTitle("SE HA AGREGADO UN DESTINO AL RECORRIDO")
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(0,builder.build());

    }



}
