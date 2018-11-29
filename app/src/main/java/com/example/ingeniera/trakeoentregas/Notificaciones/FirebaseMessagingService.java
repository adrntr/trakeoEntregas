package com.example.ingeniera.trakeoentregas.Notificaciones;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.ingeniera.trakeoentregas.R;
import com.google.firebase.messaging.RemoteMessage;


public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        showNotification(remoteMessage.getData().get("message"));
        Log.i("FirebaseMessagin",remoteMessage.getData().get("message"));

    }

    private void showNotification(String message) {

        /*Intent i = new Intent(this,CamaraVivo.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,i, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent i2= new Intent(this,Ingreso.class);
        i2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent2 = PendingIntent.getActivity(this,0,i2, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentText(message)
                .setSmallIcon(R.drawable.logo)
                .setDefaults(Notification.DEFAULT_VIBRATE);

        switch (message){
            case "Alerta de presencia desconocida":
                builder.setContentTitle("ATENCIÓN - ALARMA");
                builder.setContentIntent(pendingIntent);
                break;
            case "La batería se encuentra desconectada":
                builder.setContentTitle("ATENCIÓN - CONEXION");
                builder.setContentIntent(pendingIntent2);
                break;
            case "La batería se encuentra con baja carga":
                builder.setContentTitle("ATENCIÓN - BATERIA");
                builder.setContentIntent(pendingIntent2);
                break;
        }

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(0,builder.build());
        */
    }


}
