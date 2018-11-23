package com.example.ingeniera.trakeoentregas;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

import static com.example.ingeniera.trakeoentregas.SolicitarDestinos.almacenDestinos;

public class TransporteInfo extends AppCompatActivity {

    TextView transporteTv,direccionTv,clienteTv,cantidadTv,tipoTv,telefonoTv,cierreTV,idDestinoTv;

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

        Bundle extras = getIntent().getExtras();
        int idDestino=extras.getInt("idDestino");

        ArrayList<Destinos> destinos = almacenDestinos.getArrayList("arrayDestinosKey");

        for (int i=0;i<destinos.size();i++){
            if(destinos.get(i).getIdCliente()==idDestino){
                Destinos destino = new Destinos();
                destino=destinos.get(i);
                transporteTv.setText(destino.getTransporte());
                direccionTv.setText(destino.getDireccion_transporte());
                clienteTv.setText(destino.getNombre_cliente());
                cantidadTv.setText(String.valueOf(destino.getCantidadBultos()));
                tipoTv.setText("sobre");
                telefonoTv.setText("telefono");
                cierreTV.setText("horario");
                idDestinoTv.setText(String.valueOf(destino.getIdCliente()));

            }
        }


    }
}
