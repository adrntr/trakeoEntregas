package com.example.ingeniera.trakeoentregas.Entregas;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ingeniera.trakeoentregas.Destino.Destinos;
import com.example.ingeniera.trakeoentregas.Destino.DireccionesMapsApi;
import com.example.ingeniera.trakeoentregas.Destino.ListaDestinos;
import com.example.ingeniera.trakeoentregas.R;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.PublicKey;
import java.util.ArrayList;

import static com.example.ingeniera.trakeoentregas.Ingreso.SolicitarDestinos.almacenDestinos;
import static com.google.android.gms.vision.CameraSource.CAMERA_FACING_BACK;

public class QrReader extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 213;
    SurfaceView surfaceView;
    CameraSource cameraSource;
    TextView textView, actividadTv, codigoTv;
    EditText ingresoEt;
    Button entregarBt;
    BarcodeDetector barcodeDetector;
    Boolean enviando = false;
    long timeUltimoUso = System.currentTimeMillis();

    SurfaceHolder surfaceHolderGlobal;
    private static final int CAMERA_REQUEST = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_reader);

        surfaceView = findViewById(R.id.surfaceView);
        textView = findViewById(R.id.textView);
        actividadTv = findViewById(R.id.actividadTv);
        codigoTv = findViewById(R.id.codigoTv);
        ingresoEt=findViewById(R.id.ingresoEt);
        entregarBt=findViewById(R.id.entregarBt);

        entregarBt.setOnClickListener(clicListener);
        setTitle("ENTREGAS - "+ almacenDestinos.getUsuario("nombreApellidoKey"));

        almacenDestinos.setGoogleMapsApp(false);

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1000000, 1000000)
                .setAutoFocusEnabled(true)
                .setFacing(CAMERA_FACING_BACK)
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {


            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {

                surfaceHolderGlobal = surfaceHolder;

                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(QrReader.this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);
                    return;
                }
                    try {
                        cameraSource.start(surfaceHolder);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrCodes = detections.getDetectedItems();
                if (qrCodes.size() != 0 && System.currentTimeMillis() > timeUltimoUso + 5000) {
                    timeUltimoUso = System.currentTimeMillis();
                    textView.post(new Runnable() {
                        @Override
                        public void run() {
                            Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(1000);
                            String actCod = qrCodes.valueAt(0).displayValue;
                            JSONObject id_externo = null;
                            String tipo = null,id_externoString = null;
                            try {
                                id_externo=new JSONObject(actCod);
                                textView.setText(actCod);
                                tipo=id_externo.optString("tipo");
                                id_externoString=id_externo.optString("id_externo");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if(tipo==null||tipo.equals("")) {
                                textView.setText("Este código no pertenece a ANDIF");
                                actividadTv.setText("ACTIVIDAD");
                                codigoTv.setText("CÓDIGO");
                            } else {
                                actividadTv.setText(tipo);
                                codigoTv.setText(id_externoString);
                                switch (tipo) {
                                    case "TRANSPORTE":
                                        ArrayList<Destinos> destinos = almacenDestinos.getArrayList("arrayDestinosKey");
                                        Boolean noEsta=true;
                                        for(int i=0;i<destinos.size();i++){
                                            if (destinos.get(i).getId_externo()==Integer.parseInt(id_externoString)){
                                                noEsta=false;
                                                TaskConsultarQrCode taskConsultarQrCode = new TaskConsultarQrCode(QrReader.this,destinos,i);
                                                taskConsultarQrCode.execute(String.valueOf(destinos.get(i).getId_externo()),String.valueOf(1),"1");
                                            }
                                        }
                                        if (noEsta) {
                                            Toast.makeText(QrReader.this, "No se encuentra", Toast.LENGTH_SHORT).show();
                                        }
                                        break;
                                    default:
                                        Toast.makeText(QrReader.this, "Intente Nuevamente", Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    View.OnClickListener clicListener =new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.entregarBt:
                    ArrayList<Destinos> destinos = almacenDestinos.getArrayList("arrayDestinosKey");
                    int id=Integer.parseInt(ingresoEt.getText().toString());
                    Boolean noEsta=true;
                    for(int i=0;i<destinos.size();i++){
                        if (destinos.get(i).getId()==id){
                            noEsta=false;
                            TaskConsultarQrCode taskConsultarQrCode = new TaskConsultarQrCode(QrReader.this,destinos,i);
                            taskConsultarQrCode.execute(String.valueOf(destinos.get(i).getId_externo()),
                                    String.valueOf(destinos.get(i).getId_tipo_registro()),
                                    "1");
                        }
                    }
                    if (noEsta){
                        Toast.makeText(QrReader.this,"No se encuentra",Toast.LENGTH_SHORT).show();
                    }
            }
        }
    };

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(QrReader.this, "Permiso aceptado", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(QrReader.this, QrReader.class);
                    startActivity(i);
                    finish();
                } else {

                    Toast.makeText(QrReader.this, "Permiso NO aceptado", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        try{
            cameraSource.release();
            DireccionesMapsApi direccionesMapsApi=new DireccionesMapsApi(QrReader.this);
            direccionesMapsApi.irAGoogleMaps();
        }catch (Exception ex){

        }

    }




    @Override
    protected void onRestart() {
        super.onRestart();
        Intent intent=new Intent(QrReader.this,QrReader.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }



}


