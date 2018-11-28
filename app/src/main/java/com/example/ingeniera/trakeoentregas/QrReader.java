package com.example.ingeniera.trakeoentregas;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

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
                if (qrCodes.size() != 0 && System.currentTimeMillis() > timeUltimoUso + 3000) {
                    timeUltimoUso = System.currentTimeMillis();
                    textView.post(new Runnable() {
                        @Override
                        public void run() {
                            Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(1000);
                            String actCod = qrCodes.valueAt(0).displayValue;
                            textView.setText(actCod);

                            String datos[] = actCod.split("\\|"); //separo el string

                            if (datos.length <= 1) {
                                textView.setText("Este código no pertenece a ANDIF");
                                actividadTv.setText("ACTIVIDAD");
                                codigoTv.setText("CÓDIGO");
                            } else {
                                actividadTv.setText(datos[0]);
                                codigoTv.setText(datos[1]);

                                TareaEnviarDatos tareaEnviarDatos = new TareaEnviarDatos();

                                switch (datos[0]) {

                                    case "TRANSPORTE":
                                        //tareaEnviarDatos.execute("https://sistemas.andif.com.ar/pruebas/prueba-remito-transporte/index.php", datos[1], "id_remito_transporte");
                                        tareaEnviarDatos.execute("http://192.168.1.176/pruebas/prueba-remito-transporte/index.php", datos[1], "id_remito_transporte");
                                        textView.setText("Enviando...");
                                        Toast.makeText(QrReader.this, "Codigo " + datos[1], Toast.LENGTH_SHORT).show();
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
                    int destino=Integer.parseInt(ingresoEt.getText().toString());
                    Boolean noEsta=true;
                    for(int i=0;i<destinos.size();i++){
                        if (destinos.get(i).getIdCliente()==destino){
                            destinos.get(i).setEntregado(true);
                            noEsta=false;
                            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                            destinos.get(i).setFechaHoraEntrega(currentDateTimeString);
                            almacenDestinos.saveArrayList(destinos);
                            Toast.makeText(QrReader.this,"Entregado a las "+currentDateTimeString,Toast.LENGTH_SHORT).show();
                            ingresoEt.setText("");

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


    /*********************************Tarea en hilo secundario para enviar id*************************/
    class TareaEnviarDatos extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {
            String linea = null;
            BufferedReader reader = null;
            String text = "";
            try {
                // Defined URL  where to send data
                URL url = new URL(params[0]);

                // Send POST data request

                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                String data = URLEncoder.encode(params[2], "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8");

                //concateno todos los parametros necesarios.
                //data += "&" + URLEncoder.encode("nombreDelDato", "UTF-8") + "="+ URLEncoder.encode(dato, "UTF-8");
                wr.write(data);
                wr.flush();

                // Get the server response

                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;

                // Read Server Response
                while ((line = reader.readLine()) != null) {
                    // Append server response in string
                    sb.append(line + "\n");
                }
                text = sb.toString();
                return text;
            } catch (Exception ex) {
                return "error";
            } finally {
                try {
                    reader.close();
                } catch (Exception ex) {
                }
            }
        }

        @Override
        protected void onPostExecute(String s) {
            enviando = false;

            if (s != null) {
                if (s.equals("error")) {
                    Toast.makeText(QrReader.this, "Intente nuevamente", Toast.LENGTH_SHORT).show();
                    textView.setText("Intente Nuevamente");
                } else {
                    textView.setText(s);
                }
            } else {
                Toast.makeText(QrReader.this, "No se pudo conectar con el servidor", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try{cameraSource.release();
        }catch (Exception ex){

        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Intent intent=new Intent(QrReader.this,QrReader.class);
        startActivity(intent);
        finish();
    }



}


