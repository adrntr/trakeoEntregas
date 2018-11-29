package com.example.ingeniera.trakeoentregas;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.example.ingeniera.trakeoentregas.Ingreso.SolicitarDestinos.almacenDestinos;

public class TransporteInfo extends AppCompatActivity {

    TextView transporteTv,direccionTv,clienteTv,cantidadTv,tipoTv,telefonoTv,cierreTV,idDestinoTv,entregadoTv,entregadoTv2;
    Button cancelarEntregaBt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transporte_info);

        transporteTv=findViewById(R.id.transporteTv);
        direccionTv=findViewById(R.id.direccionTv);
        clienteTv=findViewById(R.id.clienteTv);
        cantidadTv=findViewById(R.id.cantidadTv);
        tipoTv=findViewById(R.id.tipoTv);
        telefonoTv=findViewById(R.id.telefonoTv);
        cierreTV=findViewById(R.id.cierreTv);
        idDestinoTv=findViewById(R.id.idDestinoTv);
        cancelarEntregaBt=findViewById(R.id.cancelarEntregaBt);
        entregadoTv=findViewById(R.id.entregadoTv);
        entregadoTv2=findViewById(R.id.entregadoTv3);

        cancelarEntregaBt.setOnClickListener(clicListener);

        Bundle extras = getIntent().getExtras();
        int idDestino=extras.getInt("idDestino");

        ArrayList<Destinos> destinos = almacenDestinos.getArrayList("arrayDestinosKey");

        for (int i=0;i<destinos.size();i++){
            if(destinos.get(i).getId()==idDestino){
                Destinos destino = new Destinos();
                destino=destinos.get(i);
                transporteTv.setText(destino.getNombre_transporte());
                direccionTv.setText(destino.getDireccion_transporte());
                clienteTv.setText(destino.getNombre_cliente());
                cantidadTv.setText(String.valueOf(destino.getCantidad()));
                tipoTv.setText("Entrega Sobre");
                telefonoTv.setText("155123123");
                cierreTV.setText("18:00");
                idDestinoTv.setText(String.valueOf(destino.getId()));
                if(destino.getEntregado()){
                    cancelarEntregaBt.setVisibility(View.VISIBLE);
                    entregadoTv.setVisibility(View.VISIBLE);
                    entregadoTv2.setVisibility(View.VISIBLE);
                    entregadoTv.setText(destino.getFechaHoraEntrega());
                }

            }
        }


    }

    View.OnClickListener clicListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.cancelarEntregaBt:
                    ArrayList<Destinos> destinos = almacenDestinos.getArrayList("arrayDestinosKey");
                    int ID= Integer.parseInt(idDestinoTv.getText().toString());
                    for (int i=0;i<destinos.size();i++){
                        if(destinos.get(i).getId()==ID){
                            destinos.get(i).setEntregado(false);
                            almacenDestinos.saveArrayList(destinos);
                            cancelarEntregaBt.setVisibility(View.GONE);
                            Toast.makeText(TransporteInfo.this,"Entrega cancelada",Toast.LENGTH_SHORT).show();
                        }
                    }

            }
        }
    };
}
